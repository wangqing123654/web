package jdo.bil;



import java.sql.Timestamp;
import java.util.HashMap;

import jdo.ins.INSIbsTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.TypeTool;

/**
*
* <p>Title: �������뱨��</p>
*
* <p><b>Description:</b>
* <br>�����ѷ�������
* </p>
*
* <p>Copyright: </p>
*
* <p>Company: bluecore </p>
*
* @alias�������뱨��
* @author design: pangben 2013-8-22
* <br> coding:
* @version 4.0
*/
public class BILFinanceTool extends TJDOTool{
	/**
	 * ʵ��
	 */
	public static BILFinanceTool instanceObject;
	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static BILFinanceTool getInstance() {
		if (instanceObject == null)
			instanceObject = new BILFinanceTool();
		return instanceObject;
	}
	  /**
     * ������
     */
    public BILFinanceTool() {
    	setModuleName("bil\\BILFinanceModule.x");
        onInit();
    }
    /**
     * ʵ�����ݲ�ѯ
     * @param parm
     * @return
     */
    public TParm onQueryByPaid(TParm parm){
    	int type=parm.getInt("TYPE");
    	String sql="";
    	String dateName="";
    	String group="";
    	//String groupTem="";
    	//String jmGroup="";
    	double reduceAmt=0.00;
    	String typeSql =
            " SELECT ID, CHN_DESC AS NAME  " +
            "   FROM SYS_DICTIONARY  WHERE GROUP_ID = 'BIL_PAYTYPE' ORDER BY ID";
    	TParm typeParm=new TParm(TJDODBTool.getInstance().select(typeSql));
    	String where="";
    	if (null!=parm.getValue("MR_NO")&&parm.getValue("MR_NO").length()>0) {
    		where+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
		}
    	if (type==1) {
    		if (null!=parm.getValue("OPD_TYPE")&&parm.getValue("OPD_TYPE").length()>0) {
    			if (parm.getValue("OPD_TYPE").equals("2")) {//����
    				where+=" AND B.ADM_TYPE='O' ";    // modify by kangy 20161101 �޸�AΪB �������ݵ���ʵ������ֻ��������ﱨ�� 
    			}else if(parm.getValue("OPD_TYPE").equals("3")){
    				where+=" AND B.ADM_TYPE='E' ";// modify by kangy 20161101 �޸�AΪB �������ݵ���ʵ������ֻ��������ﱨ�� 
    			}
    		}
    		if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").equals("2")) {
			}else{
	    		String sqlr="SELECT SUM(B.REDUCE_AMT) AS REDUCE_AMT  FROM REG_PATADM A,BIL_OPB_RECP B WHERE A.CASE_NO=B.CASE_NO" +
	    		" AND A.REGCAN_USER IS NULL AND B.RESET_RECEIPT_NO IS NULL AND B.TOT_AMT>0 "+where+" AND B.RECEIPT_NO IS NOT NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE('"
	    		+ parm.getValue("START_DATE")
	    		+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
	    		+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') ";;
	    		TParm parmRe=new TParm(TJDODBTool.getInstance().select(sqlr));
	    		if (parmRe.getCount()>0) {
	    			reduceAmt=parmRe.getDouble("REDUCE_AMT",0);
	    		}
			}
		}
    	String odiMemTpye="";
    	if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").length()>0 &&
				!parm.getValue("MEM_TYPE").equals("1")) {
    		if (type==1) {
    			if (parm.getValue("MEM_TYPE").equals("3")) {//����
    				where+=" AND B.MEM_PACK_FLG ='N'  ";
    			}else if(parm.getValue("MEM_TYPE").equals("2")){//����
    				where+=" AND B.MEM_PACK_FLG ='Y' AND B.TOT_AMT <>0 ";
    			}
			}else{
				if (parm.getValue("MEM_TYPE").equals("3")) {//����
					odiMemTpye="3";
				}else if(parm.getValue("MEM_TYPE").equals("2")){//����
					odiMemTpye="2";
				}
			}
			
		}
    	String whereU="";
    	boolean flg=false;
    	TParm typeStringParm=new TParm();
    	switch (type) {
		case 1://����
			
	    	typeStringParm.setData("C0","B.PAY_TYPE01");
	    	typeStringParm.setData("C1","B.PAY_TYPE02");
	    	typeStringParm.setData("T0","B.PAY_TYPE06");
	    	typeStringParm.setData("C4","B.PAY_TYPE04");// add by kangy 20161031 �޸�ҽԺ�渶δͳ�Ƶ�������ʵ��
	    	typeStringParm.setData("EKT","B.PAY_MEDICAL_CARD+B.PAY_TYPE11");//add by huangtt 20171225 ��Ӷ���֧����ʽ�е�ҽ�ƿ�
	    	//typeStringParm.setData("GBE","B.PAY_MEDICAL_CARD+B.PAY_OTHER4+B.PAY_OTHER3");
	    	typeStringParm.setData("LPK","B.PAY_OTHER3+B.PAY_TYPE05");
	    	typeStringParm.setData("XJZKQ","B.PAY_OTHER4+B.PAY_TYPE07");
	    	typeStringParm.setData("BXZF","B.PAY_TYPE08"); //add by huangtt 20151027
//	    	typeStringParm.setData("JM","B.REDUCE_AMT");
	    	typeStringParm.setData("TC","B.MEM_PACK_FLG ='Y' AND B.TOT_AMT");
	    	// add by kangy 20171009 ���΢��֧������������
	    	typeStringParm.setData("WX","B.PAY_TYPE09");
	    	typeStringParm.setData("ZFB","B.PAY_TYPE10");
			dateName=" AND B.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
			sql="SELECT ROW_NUMBER() OVER(ORDER BY A.CASE_NO ) AS TID,A.* FROM ( ";
			TParm typeWhereStringParm =new TParm();
			for (int i = 0; i < typeStringParm.getNames().length; i++) {
//				if (typeStringParm.getNames()[i].equals("EKT") || typeStringParm.getNames()[i].equals("GBE")) {
//					if(typeStringParm.getNames()[i].equals("EKT")){
//						typeWhereStringParm.setData(typeStringParm.getNames()[i],typeStringParm.getValue(typeStringParm.getNames()[i]));
//					}
//					if(typeStringParm.getNames()[i].equals("GBE")){
//						typeWhereStringParm.setData(typeStringParm.getNames()[i]," B.PAY_OTHER1<>0 AND "+typeStringParm.getValue(typeStringParm.getNames()[i]));
//					}
//				}else{
//					typeWhereStringParm.setData(typeStringParm.getNames()[i],typeStringParm.getValue(typeStringParm.getNames()[i]));
//				}
				
				typeWhereStringParm.setData(typeStringParm.getNames()[i],typeStringParm.getValue(typeStringParm.getNames()[i]));
				
			}
			for (int i = 0; i < typeParm.getCount(); i++) {
				if (typeParm.getValue("ID",i).equals("TCZK") ||
						typeParm.getValue("ID",i).equals("YJJ")//||typeParm.getValue("ID",i).equals("C4")
						|| typeParm.getValue("ID",i).equals("GBE")
						||typeParm.getValue("ID",i).equals("JM")) {
					continue;
				}
				if (null!=parm.getValue("BIL_PAYTYPE")&&parm.getValue("BIL_PAYTYPE").length()>0) {
		    		if (!typeParm.getValue("ID",i).equals(parm.getValue("BIL_PAYTYPE"))) {
		    			continue;
					}
				}
				
				if (typeParm.getValue("ID",i).equals("TC")) {
					flg=true;// add by kangy 20171116 ָ��֧����ʽΪ�ײͽ�תʱ Ϊѭ�����UNION ALL
					if (flg) {
						whereU=" UNION ALL ";
					}
					flg=true;
					String [] array={"OPD_ORDER_HISTORY ","OPD_ORDER "};
					String [] whereArrayTwo={" B.RESET_RECEIPT_NO IS NOT NULL","B.RESET_RECEIPT_NO IS NULL AND B.CASE_NO = K.CASE_NO(+)"};
					for (int j = 0; j < whereArrayTwo.length; j++) {
						sql+=whereU+"SELECT '001' AS TYPE_ID,'����' AS TYPE," +
//							"'"+typeParm.getValue("NAME",i)+"' AS BILL_TYPE,"+
							"L.PACKAGE_DESC AS BILL_TYPE,"+ //modify by huangtt 20151010 �ײͽ�ת֧����ʽ��ӽ�ת���ײ�����
//							"'"+typeParm.getValue("ID",i)+"' AS BILL_ID," +
							"L.PACKAGE_CODE AS BILL_ID," + //modify by huangtt 20151010 �ײͽ�ת֧����ʽ��ӽ�ת���ײ�����
	                		"A.CASE_NO,A.MR_NO,D.PAT_NAME,A.DEPT_CODE,E.DEPT_CHN_DESC AS DEPT_DESC,E.DEPT_CHN_DESC,A.DR_CODE," +//E.DEPT_CHN_DESC AS DEPT_DESC
	                		"H.USER_NAME AS DR_DESC,A.DEPT_CODE AS EXE_DEPT_CODE,E.DEPT_CHN_DESC AS EXE_DEPT_DESC," +
	                		"F.CLINICAREA_CODE,G.CLINIC_DESC,J.USER_NAME AS PRINT_NAME,B.BILL_DATE," +
	                		"B.PRINT_NO," +
	                		"B.TOT_AMT," + //del by huangtt 20151010 ʹ�� opd_order���е�ar_amt
	                		//" SUM(K.AR_AMT) TOT_AMT," +
	                		"B.ACCOUNT_SEQ,B.ACCOUNT_DATE,B.CASHIER_CODE AS PRINT_USER," +
	                		"'N' INS_FLG,CASE WHEN B.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD, " +
	    					" 'δ���' LOAD_DOWN,B.REMARK01,B.REMARK02,B.REMARK03,B.REMARK04,B.REMARK05,B.REMARK06,B.REMARK07,B.REMARK08,B.REMARK09,B.REMARK10 " +//add by kangy 20170711					
	                		"FROM REG_PATADM A,BIL_OPB_RECP B,SYS_PATINFO D,SYS_DEPT E,REG_CLINICROOM F," +
	                		"REG_CLINICAREA G,SYS_OPERATOR H,SYS_OPERATOR J  " +
	                		"  ,"+array[j]+" K,MEM_PAT_PACKAGE_SECTION_D L " + //add by huangtt 20151010���OPD_ORDER��MEM_PAT_PACKAGE_SECTION_D��
	                		"WHERE A.CASE_NO=B.CASE_NO " +
	                		"AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+">0  AND " + whereArrayTwo[j] +
	                		" AND A.MR_NO=D.MR_NO " +
	                		"AND A.DEPT_CODE=E.DEPT_CODE(+) AND B.CASHIER_CODE=J.USER_ID(+) " +
	                		"AND A.CLINICROOM_NO=F.CLINICROOM_NO(+) AND A.CLINICAREA_CODE=G.CLINICAREA_CODE(+) AND A.DR_CODE=H.USER_ID " +
	                		"AND B.RECEIPT_NO = K.RECEIPT_NO(+)  AND K.MEM_PACKAGE_ID = L.ID(+) " + //add by huangtt 20151010  
	                		"AND REGCAN_USER IS NULL AND B.ACCOUNT_SEQ IS NOT NULL "+dateName+where+
	                		" GROUP BY A.CASE_NO,A.MR_NO,D.PAT_NAME,A.DEPT_CODE,E.DEPT_CHN_DESC ,E.DEPT_CHN_DESC,A.DR_CODE,H.USER_NAME ," + //add by huangtt 20151010
	                		"A.DEPT_CODE ,E.DEPT_CHN_DESC , F.CLINICAREA_CODE, G.CLINIC_DESC,J.USER_NAME ,B.BILL_DATE,B.PRINT_NO," +//add by huangtt 20151010
	                		"L.PACKAGE_CODE ,L.PACKAGE_DESC ,B.ACCOUNT_SEQ, B.ACCOUNT_DATE, B.CASHIER_CODE , B.BIL_FINANCE_FLG,B.TOT_AMT, "+//add by huangtt 20151010
					        " B.REMARK01,B.REMARK02,B.REMARK03,B.REMARK04,B.REMARK05,B.REMARK06,B.REMARK07,B.REMARK08,B.REMARK09,B.REMARK10 ";// add by kangy 20170711
					}
					
					sql +=whereU+ "SELECT '001' AS TYPE_ID,'����' AS TYPE, L.PACKAGE_DESC AS BILL_TYPE, L.PACKAGE_CODE AS BILL_ID, A.CASE_NO," +
							" A.MR_NO, D.PAT_NAME, A.DEPT_CODE, E.DEPT_CHN_DESC AS DEPT_DESC, E.DEPT_CHN_DESC, A.DR_CODE, H.USER_NAME AS DR_DESC," +
							" A.DEPT_CODE AS EXE_DEPT_CODE, E.DEPT_CHN_DESC AS EXE_DEPT_DESC, F.CLINICAREA_CODE, G.CLINIC_DESC," +
							" J.USER_NAME AS PRINT_NAME, Q.BILL_DATE, Q.PRINT_NO, Q.TOT_AMT, Q.ACCOUNT_SEQ, Q.ACCOUNT_DATE," +
							" Q.CASHIER_CODE AS PRINT_USER,'N' INS_FLG, CASE WHEN Q.BIL_FINANCE_FLG = 'Y' THEN '���ϴ�'  ELSE 'δ�ϴ�' END UP_LOAD," +
							" 'δ���' LOAD_DOWN,Q.REMARK01,Q.REMARK02,Q.REMARK03,Q.REMARK04,Q.REMARK05,Q.REMARK06,Q.REMARK07,Q.REMARK08,Q.REMARK09,Q.REMARK10 " +//add by kangy 20170711					
							" FROM REG_PATADM A, SYS_PATINFO D, SYS_DEPT E, REG_CLINICROOM F, REG_CLINICAREA G, SYS_OPERATOR H, SYS_OPERATOR J," +
							" OPD_ORDER_HISTORY K, MEM_PAT_PACKAGE_SECTION_D L," +
							" (SELECT A.RECEIPT_NO, A.CASE_NO, A.MR_NO, C.ACCOUNT_DATE_SUM AS ACCOUNT_DATE, C.ACCOUNT_SEQ, C.CASHIER_CODE," +
							" C.BIL_FINANCE_FLG, C.TOT_AMT, C.PRINT_NO, C.BILL_DATE," +
							" C.REMARK01,C.REMARK02,C.REMARK03,C.REMARK04,C.REMARK05,C.REMARK06,C.REMARK07,C.REMARK08,C.REMARK09,C.REMARK10 " +// add by 20170711
							" FROM BIL_OPB_RECP A, (SELECT B.RECEIPT_NO, B.ACCOUNT_SEQ," +
							" TO_CHAR (B.ACCOUNT_DATE, 'YYYYMMDD') ACCOUNT_DATE, B.ACCOUNT_DATE AS ACCOUNT_DATE_SUM, B.CASHIER_CODE," +
							" B.BIL_FINANCE_FLG, B.TOT_AMT, B.PRINT_NO, B.BILL_DATE," +
							" B.REMARK01,B.REMARK02,B.REMARK03,B.REMARK04,B.REMARK05,B.REMARK06,B.REMARK07,B.REMARK08,B.REMARK09,B.REMARK10 " +// add by kangy 20170711
							" FROM BIL_OPB_RECP B" +
							" WHERE "+typeStringParm.getValue(typeParm.getValue("ID",i))+" < 0 "+ dateName+where.replace("A.MR_NO", "B.MR_NO")+ 
							" ) C WHERE  A.RESET_RECEIPT_NO = C.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL) Q" +
							" WHERE  A.CASE_NO = Q.CASE_NO AND A.MR_NO = D.MR_NO AND A.DEPT_CODE = E.DEPT_CODE(+)" +
							" AND Q.CASHIER_CODE = J.USER_ID(+) AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)" +
							" AND A.CLINICAREA_CODE = G.CLINICAREA_CODE(+) AND A.DR_CODE = H.USER_ID" +
							" AND Q.RECEIPT_NO = K.RECEIPT_NO(+) AND K.MEM_PACKAGE_ID = L.ID(+) AND REGCAN_USER IS NULL" +
							" GROUP BY A.CASE_NO,A.MR_NO, D.PAT_NAME, A.DEPT_CODE, E.DEPT_CHN_DESC, E.DEPT_CHN_DESC, A.DR_CODE," +
							" H.USER_NAME, A.DEPT_CODE, E.DEPT_CHN_DESC, F.CLINICAREA_CODE, G.CLINIC_DESC, J.USER_NAME, Q.BILL_DATE," +
							" Q.PRINT_NO, L.PACKAGE_CODE, L.PACKAGE_DESC, Q.ACCOUNT_SEQ, Q.ACCOUNT_DATE, Q.CASHIER_CODE, Q.BIL_FINANCE_FLG,Q.TOT_AMT," +
							" Q.REMARK01,Q.REMARK02,Q.REMARK03,Q.REMARK04,Q.REMARK05,Q.REMARK06,Q.REMARK07,Q.REMARK08,Q.REMARK09,Q.REMARK10 ";// add by kangy 20170711
					
					// add by kangy 20171116 ֧����ʽָ��Ϊ�ײͽ�תʱ  ȥ����һ��ѭ��ǰ��UNION ALL
					if("TC".equals(parm.getValue("BIL_PAYTYPE"))){
						sql=sql.replaceFirst(" UNION ALL ", "");
					}
					//==start==add by kangy 20171120 ʵ����������ײ�δʹ�ò��ֵķ���
					String memWhere="";
					String opbWhere="";
					if (null!=parm.getValue("MR_NO")&&parm.getValue("MR_NO").length()>0) {
						memWhere+=" AND B.MR_NO='"+parm.getValue("MR_NO")+"'";
					}
					/*if (null!=parm.getValue("OPD_TYPE")&&parm.getValue("OPD_TYPE").length()>0) {
		    			if (parm.getValue("OPD_TYPE").equals("2")) {//����
		    				where+=" AND A.ADM_TYPE='O' ";  
		    			}else if(parm.getValue("OPD_TYPE").equals("3")){
		    				where+=" AND A.ADM_TYPE='E' ";
		    			}
		    		}*/
					if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").length()>0 &&
							!parm.getValue("MEM_TYPE").equals("1")) {
			    			if (parm.getValue("MEM_TYPE").equals("3")) {//����
			    				opbWhere+=" AND B.MEM_PACK_FLG ='N'  ";
			    			}else if(parm.getValue("MEM_TYPE").equals("2")){//����
			    				opbWhere+=" AND B.MEM_PACK_FLG ='Y' AND B.TOT_AMT <>0 ";
			    			}
			    			}
					if ("".equals(parm.getValue("OPD_TYPE"))||"2".equals(parm.getValue("OPD_TYPE"))||"1".equals(parm.getValue("OPD_TYPE"))) {
					sql+=whereU+" SELECT '001' AS TYPE_ID,'����' AS TYPE,'"+typeParm.getValue("NAME",i)+"' AS BILL_TYPE,'"+typeParm.getValue("ID",i)+"' AS BILL_ID," +
						 "'' CASE_NO,B.MR_NO,D.PAT_NAME,'011100' AS DEPT_CODE,'��ƺ��㲿' DEPT_DESC,'��ƺ��㲿' DEPT_CHN_DESC, '' DR_CODE,'' DR_DESC, " +
						 "'011100' EXE_DEPT_CODE,'��ƺ��㲿' EXE_DEPT_DESC,'' CLINICAREA_CODE,'' CLINIC_DESC,J.USER_NAME AS PRINT_NAME," +
						 "B.BILL_DATE,B.PRINT_NO, B.TOT_AMT,B.ACCOUNT_SEQ,B.ACCOUNT_DATE,B.CASHIER_CODE AS PRINT_USER,'N' INS_FLG, CASE WHEN " +
						 "B.BIL_FINANCE_FLG = 'Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN, B.REMARK01,B.REMARK02,B.REMARK03,B.REMARK04,B.REMARK05," +
						 "B.REMARK06,B.REMARK07,B.REMARK08,B.REMARK09,B.REMARK10 FROM BIL_MEM_RECP B,SYS_PATINFO D,"
						 + "SYS_OPERATOR J" +
						 " WHERE B.MR_NO=D.MR_NO AND B.MEM_PACK_FLG = 'Y' AND B.TOT_AMT > 0 " +
						 " AND B.ACCOUNT_SEQ IS NOT NULL AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"', 'YYYYMMDDHH24MISS') AND TO_DATE ('"+parm.getValue("END_DATE")+"'," +
						 " 'YYYYMMDDHH24MISS') AND B.MR_NO = D.MR_NO AND B.CASHIER_CODE = J.USER_ID(+) " +
						 "  "+memWhere+
						 " GROUP BY B.MR_NO, D.PAT_NAME,J.USER_NAME, B.BILL_DATE,B.PRINT_NO, B.ACCOUNT_SEQ,B.ACCOUNT_DATE,B.CASHIER_CODE,B.BIL_FINANCE_FLG,B.TOT_AMT, B.REMARK01," +
						 "B.REMARK02,B.REMARK03, B.REMARK04,B.REMARK05,B.REMARK06,B.REMARK07,B.REMARK08,B.REMARK09,B.REMARK10";
					}
					//==end==add by kangy 20171120 ʵ����������ײ�δʹ�ò��ֵķ���

				}else{
					if (flg) {
						whereU=" UNION ALL ";
					}
					flg=true;
					sql+=whereU+"SELECT '001' AS TYPE_ID,'����' AS TYPE," +
					"'"+typeParm.getValue("NAME",i)+"' AS BILL_TYPE,"+
	                "'"+typeParm.getValue("ID",i)+"' AS BILL_ID,A.CASE_NO,A.MR_NO,D.PAT_NAME,A.DEPT_CODE,E.DEPT_CHN_DESC AS DEPT_DESC,E.DEPT_CHN_DESC,A.DR_CODE," +//E.DEPT_CHN_DESC AS DEPT_DESC
					"H.USER_NAME AS DR_DESC,A.DEPT_CODE AS EXE_DEPT_CODE,E.DEPT_CHN_DESC AS EXE_DEPT_DESC," +
					"F.CLINICAREA_CODE,G.CLINIC_DESC,J.USER_NAME AS PRINT_NAME,B.BILL_DATE," +
					"B.PRINT_NO,"+typeStringParm.getValue(typeParm.getValue("ID",i))+" AS TOT_AMT,B.ACCOUNT_SEQ,B.ACCOUNT_DATE,B.CASHIER_CODE AS PRINT_USER,'N' INS_FLG,CASE WHEN B.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD, " +
					" 'δ���' LOAD_DOWN,B.REMARK01,B.REMARK02,B.REMARK03,B.REMARK04,B.REMARK05,B.REMARK06,B.REMARK07,B.REMARK08,B.REMARK09,B.REMARK10 " +//add by kangy 20170711					
					"FROM REG_PATADM A,BIL_OPB_RECP B,SYS_PATINFO D,SYS_DEPT E,REG_CLINICROOM F," +
					"REG_CLINICAREA G,SYS_OPERATOR H,SYS_OPERATOR J  " +
					"WHERE A.CASE_NO=B.CASE_NO " +
					"AND "+typeWhereStringParm.getValue(typeParm.getValue("ID",i))+"<>0 AND A.MR_NO=D.MR_NO " +
					"AND A.DEPT_CODE=E.DEPT_CODE(+) AND B.CASHIER_CODE=J.USER_ID(+) " +
					"AND A.CLINICROOM_NO=F.CLINICROOM_NO(+) AND A.CLINICAREA_CODE=G.CLINICAREA_CODE(+) AND A.DR_CODE=H.USER_ID " +
					"AND REGCAN_USER IS NULL AND B.ACCOUNT_SEQ IS NOT NULL "+dateName+where;
				}
			}
			group=" ) A ORDER BY A.CASE_NO";
	    	sql+=group;
			//jmGroup=",B.AR_AMT ";
	    	
			break;
		case 2://סԺ
//			if (odiMemTpye.length()>0) {
//				if (odiMemTpye.equals("3")) {//����
//					typeStringParm.setData("C0","B.PAY_CASH");
//					typeStringParm.setData("C1","B.PAY_BANK_CARD");
//					typeStringParm.setData("T0","B.PAY_CHECK");
//					typeStringParm.setData("LPK","B.PAY_GIFT_CARD");
//					typeStringParm.setData("XJZKQ","B.PAY_DISCNT_CARD");
//					typeStringParm.setData("YJJ","B.PAY_BILPAY");
//					typeStringParm.setData("JM","B.REDUCE_AMT");
//				}
//				else if (odiMemTpye.equals("2")) {//����
//					typeStringParm.setData("TC","B.LUMPWORK_AMT");
//					//typeStringParm.setData("TCZK","B.LUMPWORK_OUT_AMT");
//				}
//			}else{
//				typeStringParm.setData("C0","B.PAY_CASH");
//				typeStringParm.setData("C1","B.PAY_BANK_CARD");
//				typeStringParm.setData("T0","B.PAY_CHECK");
//				typeStringParm.setData("LPK","B.PAY_GIFT_CARD");
//				typeStringParm.setData("XJZKQ","B.PAY_DISCNT_CARD");
//				typeStringParm.setData("YJJ","B.PAY_BILPAY");
//				typeStringParm.setData("JM","B.REDUCE_AMT");
//				typeStringParm.setData("TC","B.LUMPWORK_AMT");
//				//typeStringParm.setData("TCZK","B.LUMPWORK_OUT_AMT");
//			}
			typeStringParm.setData("C0","B.PAY_CASH");
			typeStringParm.setData("C1","B.PAY_BANK_CARD");
			typeStringParm.setData("T0","B.PAY_CHECK");
			typeStringParm.setData("LPK","B.PAY_GIFT_CARD");
			typeStringParm.setData("XJZKQ","B.PAY_DISCNT_CARD");
			typeStringParm.setData("YJJ","B.PAY_BILPAY");
			typeStringParm.setData("BXZF","B.PAY_BXZF");
			typeStringParm.setData("C4","B.PAY_DEBIT");//add by kangy 20161031 
			typeStringParm.setData("WX","B.PAY_TYPE09");//add by kangy 20171009 ���΢��֧��������
			typeStringParm.setData("ZFB","B.PAY_TYPE10");
			typeStringParm.setData("EKT","B.PAY_MEDICAL_CARD");//add by kangy 20171204 סԺ�������ҽ�ƿ�֧����ʽ
			//typeStringParm.setData("JM","-B.REDUCE_AMT");
			dateName=" AND B.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS')";
			sql="SELECT ROW_NUMBER() OVER(ORDER BY A.CASE_NO) AS TID,A.*  FROM ( ";
			for (int i = 0; i < typeParm.getCount(); i++) {//�����ڵ�֧����ʽ����Ҫ����
				if (null==typeStringParm.getValue(typeParm.getValue("ID",i))||
						typeStringParm.getValue(typeParm.getValue("ID",i)).length()<=0) {
					continue;
				}
				if (null!=parm.getValue("BIL_PAYTYPE")&&parm.getValue("BIL_PAYTYPE").length()>0) {
		    		if (!typeParm.getValue("ID",i).equals(parm.getValue("BIL_PAYTYPE"))) {
		    			continue;
					}
				}
				if (flg) {
					whereU=" UNION ALL ";
				}
				flg=true;

//				if (typeParm.getValue("ID",i).equals("TC")||typeParm.getValue("ID",i).equals("TCZK")) {
//					sql+=whereU+" SELECT '002' AS TYPE_ID,'סԺ' AS TYPE ,'"+typeParm.getValue("NAME",i)+"' AS BILL_TYPE,'"+typeParm.getValue("ID",i)+"' AS BILL_ID,A.CASE_NO,A.IPD_NO,A.MR_NO,D.PAT_NAME," +
//					 "A.DEPT_CODE,E.DEPT_CHN_DESC,A.VS_DR_CODE AS DR_CODE," +
//					 "H.USER_NAME AS DR_DESC ,A.DS_DEPT_CODE AS EXE_DEPT_CODE ,F.DEPT_CHN_DESC AS DEPT_DESC,F.DEPT_CHN_DESC AS EXE_DEPT_DESC,B.CHARGE_DATE AS BILL_DATE," +
//					 "B.RECEIPT_NO AS PRINT_NO,"+typeStringParm.getValue(typeParm.getValue("ID",i))+" AS TOT_AMT,B.ACCOUNT_SEQ,B.ACCOUNT_DATE,B.CASHIER_CODE AS PRINT_USER," +
//					 "'N' INS_FLG,A.STATION_CODE AS CLINICAREA_CODE,S.STATION_DESC AS CLINIC_DESC,J.USER_NAME AS PRINT_NAME,CASE WHEN B.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD "+
//					 "FROM ADM_INP A ,BIL_IBS_RECPM B,SYS_PATINFO D,SYS_DEPT E,SYS_DEPT F,SYS_OPERATOR H,SYS_OPERATOR J,SYS_STATION S " +
//					 "WHERE A.CASE_NO=B.CASE_NO AND " +
//					 "B.CASHIER_CODE=J.USER_ID(+) AND A.STATION_CODE = S.STATION_CODE(+) AND "+
//					 "A.MR_NO=D.MR_NO AND A.DEPT_CODE=E.DEPT_CODE(+) AND " +
//					 "A.DS_DEPT_CODE=F.DEPT_CODE(+) AND A.VS_DR_CODE=H.USER_ID(+) AND (A.CANCEL_FLG ='N' OR A.CANCEL_FLG IS NULL) AND " +
//					 "B.ACCOUNT_SEQ IS NOT NULL AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+"<>0 "+dateName+where;
//				}else{
//									}
				sql+=whereU+"SELECT '002' AS TYPE_ID,'סԺ' AS TYPE ,'"+typeParm.getValue("NAME",i)+"' AS BILL_TYPE,'"+
				typeParm.getValue("ID",i)+"' AS BILL_ID,A.CASE_NO,A.IPD_NO,A.MR_NO,D.PAT_NAME," +
				 "A.DEPT_CODE,E.DEPT_CHN_DESC,A.VS_DR_CODE AS DR_CODE," +
				 "H.USER_NAME AS DR_DESC ,A.DS_DEPT_CODE AS EXE_DEPT_CODE ,F.DEPT_CHN_DESC AS DEPT_DESC,F.DEPT_CHN_DESC AS EXE_DEPT_DESC,B.CHARGE_DATE AS BILL_DATE," +
				 "B.RECEIPT_NO AS PRINT_NO,"+typeStringParm.getValue(typeParm.getValue("ID",i))+" AS TOT_AMT,B.ACCOUNT_SEQ,B.ACCOUNT_DATE,B.CASHIER_CODE AS PRINT_USER," +
				 "'N' INS_FLG,A.STATION_CODE AS CLINICAREA_CODE,S.STATION_DESC AS CLINIC_DESC,J.USER_NAME AS PRINT_NAME,CASE WHEN B.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD," +
				 "'δ���' LOAD_DOWN,B.MEMO1 REMARK01,B.MEMO2 REMARK02,B.MEMO3 REMARK03,B.MEMO4 REMARK04,B.MEMO5 REMARK05,B.MEMO6 REMARK06,B.MEMO7 REMARK07,B.MEMO8 REMARK08,B.MEMO9 REMARK09,B.MEMO10 REMARK10 "+// add by kangy 20170712
				 "FROM ADM_INP A ,BIL_IBS_RECPM B,SYS_PATINFO D,SYS_DEPT E,SYS_DEPT F,SYS_OPERATOR H,SYS_OPERATOR J,SYS_STATION S " +
				 "WHERE A.CASE_NO=B.CASE_NO AND  " +
				 "B.CASHIER_CODE=J.USER_ID(+) AND A.STATION_CODE = S.STATION_CODE(+) AND "+
				 "A.MR_NO=D.MR_NO AND A.DEPT_CODE=E.DEPT_CODE(+) AND " +
				 "A.DS_DEPT_CODE=F.DEPT_CODE(+) AND A.VS_DR_CODE=H.USER_ID(+) AND (A.CANCEL_FLG ='N' OR A.CANCEL_FLG IS NULL) AND " +
				 "B.ACCOUNT_SEQ IS NOT NULL AND "+typeStringParm.getValue(typeParm.getValue("ID",i))+"<>0 "+dateName+where;
			}
			group=" ) A ORDER BY A.CASE_NO";
	    	sql+=group;
			break;

		default:
			break;
		}

    	System.out.println("SQLʵ������PANGBEN 2013-9-26::"+sql);
    	parm=new TParm(TJDODBTool.getInstance().select(sql));
    	parm.setData("REDUCE_AMT",reduceAmt);
    	return parm;
    }
    /**
     * У��Ӧ�ձ��Ƿ�������
     * @param parm
     * @return
     */
    public TParm checkDiIncomeexp(TParm parm){
    	TParm result=query("checkDiIncomeexp",parm,"javahisHRP");
    	return result;
    }
    /**
     * У���м���Ƿ��Ѿ����أ��Ѿ����ص����ݲ����Ե���
     */
    public TParm checkLogDrlogAyh(TParm parm){
    	TParm result=query("checkLogDrlogAyh",parm,"javahisHRP");
    	return result;
    }
    /**
     * 
    * @Title: insertPatchHrpLog
    * @Description: TODO(��������HRP���γ��������¼��ʷ����)
    * @author pangben
    * @return
    * @throws
     */
    public TParm insertPatchHrpLog(TParm parm){
    	TParm result =update("insertPatchHrpLog",parm);
    	return result;
    }
    /**
     * У��Ӧ�ձ��Ƿ�������
     * @param parm
     * @return
     */
    public TParm checkDiIncomeexp(TParm parm,TConnection connection){
    	TParm result=query("checkDiIncomeexp",parm,connection);
    	return result;
    }
    /**
     * У���̯�Ƿ�������
     * @param parm
     * @return
     */
    public TParm checkApportion(TParm parm){
    	String sql="SELECT PK_SERVICEVOLEXP FROM DI_SERVICEVOLEXP WHERE BATCHNAME='"
    		+parm.getValue("BATCHNAME")+"' AND TYPE='"+parm.getValue("TYPE")+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * ɾ����̯���Ƿ�������
     * @param parm
     * @param connection
     * @return
     */
    public TParm deleteApportion(TParm parm,TConnection connection){
    	TParm result =update("deleteApportion",parm,connection);
    	return result;
    }
    /**
     * У��ʵ�ձ��Ƿ�������
     */
    public TParm checkDiIncomereal(TParm parm,TConnection connection){
    	TParm result=query("checkDiIncomereal",parm,connection);
    	return result;
    }
    /**
     * У��ʵ�ձ��Ƿ�������
     */
    public TParm checkDiIncomereal(TParm parm){
    	TParm result=query("checkDiIncomereal",parm,"javahisHRP");
    	return result;
    }
    /**
     * У��Ԥ������Ƿ�������(����)
     */
    public TParm checkDiIncomepreO(TParm parm){
    	TParm result=query("checkDiIncomepreO",parm,"javahisHRP");
    	return result;
    }
    /**
     * У��Ԥ������Ƿ������ݣ�סԺ��
     */
    public TParm checkDiIncomepre(TParm parm){
    	TParm result=query("checkDiIncomepre",parm,"javahisHRP");
    	return result;
    }
    /**
     * У��Ԥ������Ƿ�������
     */
    public TParm checkDiIncomepre(TParm parm,TConnection connection){
    	TParm result=query("checkDiIncomepre",parm,connection);
    	return result;
    }
    /**
     * У��Ԥ������Ƿ�������
     */
    public TParm checkDiIncomepreO(TParm parm,TConnection connection){
    	TParm result=query("checkDiIncomepreO",parm,connection);
    	return result;
    }
    
    /**
     * У���ֵ�Ĳı��Ƿ�������add by huangjw 20141112
     */
    public TParm checkDiIncomeHigh(TParm parm){
    	TParm result=query("checkDiIncomeHigh",parm,"javahisHRP");
    	return result;
    }
    
    /**
     * У���ֵ�Ĳı��Ƿ�������add by huangjw 20141112
     */
    public TParm checkDiIncomeHigh(TParm parm,TConnection connection){
    	TParm result=query("checkDiIncomeHigh",parm,connection);
    	return result;
    }
    /**'
     * У��ɱ�������Ƿ������� add by huangjw 20150408
     * @param parm
     * @return
     */
    public TParm checkDiIncomeCostAcount(TParm parm){
    	TParm result=query("checkDiIncomeCostAcount",parm,"javahisHRP");
    	return result;
    }
    /**
     * У��ҩƷ�����Ƿ�������
     * @param parm
     * @return
     */
    public TParm checkDidrugs(TParm parm){
    	TParm result=query("checkDidrugs",parm,"javahisHRP");
    	return result;
    }
    /**'
     * У��ɱ�������Ƿ������� add by huangjw 20150408
     * @param parm
     * @return
     */
    public TParm checkDiIncomeCostAcount(TParm parm,TConnection connection){
    	TParm result=query("checkDiIncomeCostAcount",parm,connection);
    	return result;
    }
    /**
     * ����Ӧ������
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertDiIncomeexp(TParm parm,TConnection connection){
    	TParm result =update("insertDiIncomeexp",parm,connection);
    	return result;
    }
    /**
     * ����ʵ������
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertDiIncomereal(TParm parm,TConnection connection){
    	TParm result =update("insertDiIncomereal",parm,connection);
    	return result;
    }
    /**
     * ����Ԥ��������
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertDiIncomepre(TParm parm,TConnection connection){
    	TParm result =update("insertDiIncomepre",parm,connection);
    	return result;
    }
    
    /**
     *������ֵ�Ĳ����� add by huangjw 20141114 
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertDiIncomeHigh(TParm parm,TConnection connection){
    	TParm result =update("insertDiIncomeHigh",parm,connection);
    	return result;
    }
    
    /**
     * �����ɱ��������� add by huangjw 20150410
     * @param parm
     * @param connection
     * @return
     */
    public TParm insertDiIncomeCostAcount(TParm parm,TConnection connection){
    	TParm result=update("insertDiIncomeCostAcount",parm,connection);
    	return result;
    }
    /**
     * ����xml�ļ�
     * @param parm
     * @return
     */
    public TParm onQueryCostXml(TParm parm){
//    	int type=parm.getInt("TYPE");
//    	String sql="";
//    	String dateName="";
//    	String group="";
//    	switch (type) {
//		case 1://�Һ�
//			sql="SELECT COUNT(A.DEPT_CODE) qty ,'��������' sharepara,E.DEPT_DESC AS deptbas,SUM(B.PAY_CASH) AS measure, " +
//					"'ÿ�¿�������EXCEL����' sourceSystem " +
//					"FROM REG_PATADM A,REG_RECEIPT B,SYS_DEPT E,REG_CLINICROOM F," +
//					"REG_CLINICAREA G,SYS_OPERATOR H,SYS_OPERATOR J " +
//					"WHERE A.HOSP_AREA=B.HOSP_AREA AND A.ADM_TYPE=B.ADM_TYPE AND A.CASE_NO=B.CASE_NO " +
//					"AND B.RESET_RECEIPT_NO IS NULL AND B.PAY_CASH>0 " +
//					"AND A.DEPT_CODE=E.DEPT_CODE(+) AND A.HOSP_AREA=F.HOSP_AREA(+) AND A.ADM_TYPE=F.ADM_TYPE(+) " +
//					"AND B.CASHIER_CODE=J.USER_ID(+) " +
//					"AND A.CLINICROOM_NO=F.CLINICROOM_NO(+) AND F.CLINICAREA_CODE=G.CLINICAREA_CODE(+) AND A.DR_CODE=H.USER_ID " +
//					"AND REGCAN_USER IS NULL AND B.ACCOUNT_SEQ IS NOT NULL";
//			dateName=" AND B.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
//			group=" GROUP BY E.DEPT_DESC";
//			break;
//		case 2://����
//			sql="SELECT  COUNT(A.DEPT_CODE) qty ,'��������' sharepara,E.DEPT_DESC AS deptbas,SUM(B.AR_AMT) AS measure, " +
//					"'ÿ�¿�������EXCEL����' sourceSystem " +
//			"FROM REG_PATADM A,OPB_RECEIPT B,SYS_DEPT E,REG_CLINICROOM F," +
//			"REG_CLINICAREA G,SYS_OPERATOR H,SYS_OPERATOR J  " +
//			"WHERE A.HOSP_AREA=B.HOSP_AREA AND A.ADM_TYPE=B.ADM_TYPE AND A.CASE_NO=B.CASE_NO " +
//			"AND B.RESET_RECEIPT_NO IS NULL " +
//			"AND A.DEPT_CODE=E.DEPT_CODE(+) AND A.HOSP_AREA=F.HOSP_AREA(+) AND A.ADM_TYPE=F.ADM_TYPE(+) " +
//			"AND A.CLINICROOM_NO=F.CLINICROOM_NO(+) AND F.CLINICAREA_CODE=G.CLINICAREA_CODE(+) AND A.DR_CODE=H.USER_ID " +
//			"AND B.CASHIER_CODE=J.USER_ID(+) " +
//			"AND REGCAN_USER IS NULL AND B.ACCOUNT_SEQ IS NOT NULL";
//			dateName=" AND B.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
//			group=" GROUP BY E.DEPT_DESC";
//			break;
//		case 3://סԺ
//			sql="SELECT COUNT(A.DEPT_CODE) qty ,'��������' sharepara,E.DEPT_DESC AS deptbas,SUM(L.TOT_AMT) AS measure, " +
//					"'ÿ�¿�������EXCEL����' sourceSystem " +
//                " FROM ADM_INP A ,SYS_DEPT E,SYS_DEPT F,SYS_OPERATOR H, "+
//                " IBS_ORDM K,IBS_ORDD L,SYS_CHARGE N,SYS_STATION S "+
//                " WHERE K.CASE_NO=L.CASE_NO AND K.CASE_NO=A.CASE_NO AND L.REXP_CODE = N.CHARGE_CODE "+
//                " AND N.ADM_TYPE='I' AND K.CASE_NO_SEQ=L.CASE_NO_SEQ  AND A.IN_DEPT_CODE = S.DEPT_CODE(+) "+
//                " AND A.IN_DEPT_CODE=E.DEPT_CODE(+) AND  "+
//                " A.DS_DEPT_CODE=F.DEPT_CODE(+) AND A.VS_CODE=H.USER_ID(+) AND (A.CANCEL_FLG ='N' OR A.CANCEL_FLG IS NULL) "+
//                "   AND A.DS_DATE IS NULL";
//			dateName=" AND L.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
//			group="  GROUP BY E.DEPT_DESC ";
//			break;
//			
//		default:
//			break;
//		}
//    	sql+=dateName+group;
    	String sql="select sum(B.qty) QTY,'���������ĲĹ�Ӧ����' sharepara,SUM(QTY*COST_PRICE) AS measure,a.to_org_code ," +
    			   "C.DEPT_DESC deptbas,'ÿ�¿��������ĲĹ�Ӧ��EXCEL����' sourceSystem  FROM INV_SUP_DISPENSEM A, INV_SUP_DISPENSED B,SYS_DEPT C WHERE "+
                   "A.DISPENSE_NO=B.DISPENSE_NO AND a.to_org_code=C.DEPT_CODE AND DISPENSE_DATE BETWEEN "+
                   "TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS') group by a.to_org_code,C.DEPT_DESC";
    	//System.out.println("SQL:::sdfsdf::::"+sql);
    	parm=new TParm(TJDODBTool.getInstance().select(sql));
    	return parm;
    }
    /**
     * �ɱ�����
     * @return
     */
    public TParm onQueryCost(TParm parm){
    	int type=parm.getInt("TYPE");
    	String sql="";
    	String dateName="";
    	String group="";
    	switch (type) {
		case 1://�Һ�
			sql="SELECT ROW_NUMBER() OVER(ORDER BY A.DEPT_CODE DESC) AS TID,'001' AS TYPE_ID,'�Һ�' AS TYPE, '�Һŷ�' AS BILL_TYPE,'00' AS BILL_ID," +
					"A.DEPT_CODE,E.DEPT_DESC," +
					"SUM(B.PAY_CASH) AS TOT_AMT,COUNT(A.DR_CODE) NUM " +
					"FROM REG_PATADM A,REG_RECEIPT B,SYS_DEPT E,REG_CLINICROOM F," +
					"REG_CLINICAREA G,SYS_OPERATOR H,SYS_OPERATOR J " +
					"WHERE A.HOSP_AREA=B.HOSP_AREA AND A.ADM_TYPE=B.ADM_TYPE AND A.CASE_NO=B.CASE_NO " +
					"AND B.RESET_RECEIPT_NO IS NULL AND B.PAY_CASH>0 " +
					"AND A.DEPT_CODE=E.DEPT_CODE(+) AND A.HOSP_AREA=F.HOSP_AREA(+) AND A.ADM_TYPE=F.ADM_TYPE(+) " +
					"AND B.CASHIER_CODE=J.USER_ID(+) " +
					"AND A.CLINICROOM_NO=F.CLINICROOM_NO(+) AND F.CLINICAREA_CODE=G.CLINICAREA_CODE(+) AND A.DR_CODE=H.USER_ID " +
					"AND REGCAN_USER IS NULL AND B.ACCOUNT_SEQ IS NOT NULL";
			dateName=" AND B.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
			group=" GROUP BY A.DEPT_CODE,E.DEPT_DESC";
			break;
		case 2://����
			sql="SELECT ROW_NUMBER() OVER(ORDER BY A.DEPT_CODE DESC) AS TID,A.TYPE,A.TYPE_ID,A.BILL_TYPE,A.DEPT_CODE,A.DEPT_DESC,A.TOT_AMT,A.NUM, " +
//			"CASE WHEN A.BILL_ID='121' THEN '01' WHEN A.BILL_ID='222' THEN '02' WHEN A.BILL_ID='323' THEN '03' WHEN A.BILL_ID='D21' THEN '20' " +
//			"WHEN A.BILL_ID='D22' THEN '08' WHEN A.BILL_ID='E21' THEN '11' WHEN A.BILL_ID='I21' THEN '12' WHEN A.BILL_ID='A22' THEN '04' " +
//			"WHEN A.BILL_ID='A21' THEN '05' WHEN A.BILL_ID='W21' THEN '22' WHEN A.BILL_ID='Q21' THEN '13' WHEN A.BILL_ID='S21' THEN '15' " +
//			"WHEN A.BILL_ID='M21' THEN '10' END BILL_ID "+
			"A.BILL_ID "+
			"FROM (SELECT '002' AS TYPE_ID,'����' AS TYPE," +
			"C.REXP_CODE AS BILL_ID,N.CHARGE_DESC AS BILL_TYPE,A.DEPT_CODE,E.DEPT_DESC,"+
			"SUM(C.TOT_AMT) AS TOT_AMT,COUNT(C.REXP_CODE) NUM " +
			"FROM REG_PATADM A,OPB_TRAN C,SYS_DEPT E,SYS_CHARGE N,OPB_RECEIPT B  " +
			"WHERE A.CASE_NO=C.CASE_NO AND A.CASE_NO=B.CASE_NO AND C.RECEIPT_NO=B.RECEIPT_NO " +
			"AND C.REXP_CODE = N.CHARGE_CODE AND N.ADM_TYPE='O' " +
			"AND A.DEPT_CODE=E.DEPT_CODE(+) "+
			"AND C.BILL_FLG='Y' AND C.TOT_AMT<>0" +
			"AND REGCAN_USER IS NULL  AND B.ACCOUNT_SEQ IS NOT NULL ";
			dateName=" AND B.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
			group=" GROUP BY C.REXP_CODE,A.DEPT_CODE,E.DEPT_DESC,N.CHARGE_DESC) A WHERE A.TOT_AMT<>0";
			break;
		case 3://סԺ
			sql="SELECT ROW_NUMBER() OVER(ORDER BY L.REXP_CODE DESC) AS TID,'003' AS TYPE_ID,'סԺ' AS TYPE ,"+
                " A.IN_DEPT_CODE AS DEPT_CODE,E.DEPT_DESC,L.REXP_CODE AS BILL_ID,N.CHARGE_DESC AS BILL_TYPE,"+
                " SUM(L.TOT_AMT) AS TOT_AMT,COUNT(L.REXP_CODE) NUM "+
                " FROM ADM_INP A ,SYS_DEPT E,SYS_DEPT F,SYS_OPERATOR H, "+
                " IBS_ORDM K,IBS_ORDD L,SYS_CHARGE N,SYS_STATION S "+
                " WHERE K.CASE_NO=L.CASE_NO AND K.CASE_NO=A.CASE_NO AND L.REXP_CODE = N.CHARGE_CODE "+
                " AND N.ADM_TYPE='I' AND K.CASE_NO_SEQ=L.CASE_NO_SEQ  AND A.IN_DEPT_CODE = S.DEPT_CODE(+) "+
                " AND A.IN_DEPT_CODE=E.DEPT_CODE(+) AND  "+
                " A.DS_DEPT_CODE=F.DEPT_CODE(+) AND A.VS_CODE=H.USER_ID(+) AND (A.CANCEL_FLG ='N' OR A.CANCEL_FLG IS NULL) "+
                "   AND A.DS_DATE IS NULL";
			dateName=" AND L.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
			group=" GROUP BY L.REXP_CODE,A.IN_DEPT_CODE,E.DEPT_DESC,N.CHARGE_DESC";
			break;

		default:
			break;
		}
    	sql+=dateName+group;
    	//System.out.println("SQL::�ɱ�����::::"+sql);
    	parm=new TParm(TJDODBTool.getInstance().select(sql));
    	return parm;
    }
    /**
     * ��̯����
     * @return
     */
    public TParm onApportion(TParm parm){
    	int type=parm.getInt("TYPE");
    	String sql="";
    	String dateName="";
    	String group="";
    	switch (type) {
		case 2://����
			sql="SELECT A.DEPT_CODE,E.DEPT_DESC,COUNT(A.CASE_NO) NUM, '002' AS TYPE_ID,'����' AS TYPE FROM REG_PATADM A,SYS_DEPT E WHERE A.DEPT_CODE=E.DEPT_CODE(+) AND REGCAN_USER IS NULL";
			dateName=" AND A.ADM_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"
				+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
				group=" GROUP BY A.DEPT_CODE,E.DEPT_DESC";
				break;
		case 3://סԺ
			sql="SELECT A.IN_DEPT_CODE AS DEPT_CODE,E.DEPT_DESC,COUNT(A.CASE_NO) NUM,'003' AS TYPE_ID,'סԺ' AS TYPE FROM ADM_INP A,SYS_DEPT E WHERE A.IN_DEPT_CODE=E.DEPT_CODE(+) AND (A.CANCEL_FLG ='N' OR A.CANCEL_FLG IS NULL)";
			dateName=" AND A.IN_DATE BETWEEN TO_DATE('"+parm.getValue("MONTH_DATE")+"01000000','YYYYMMDDHH24MISS') AND TO_DATE('"
			+parm.getValue("MONTH_DATE")+parm.getValue("DAY")+"235959','YYYYMMDDHH24MISS')";
			group=" GROUP BY A.IN_DEPT_CODE,E.DEPT_DESC";
			break;
    	}
    	sql+=dateName+group;
    //	System.out.println("SQL::::��̯:::"+sql);
    	parm=new TParm(TJDODBTool.getInstance().select(sql));
    	return parm;
    }
    /**
     * Ӧ�����ݲ�ѯ
     * @param parm
     * @return
     */
    public TParm onQueryByAccounts(TParm parm){
    	int type=parm.getInt("TYPE");
    	String sql="";
    	String reSql="";
    	String dateName="";
    	String reDateName="";
    	String group="";
    	String group2="";
    	String historyWhere="";
    	double reduceAmt=0.00;
    	String historyName="";
    	TParm parmRe = new TParm();
    	switch (type) {
		case 1://����
			sql = "SELECT ROW_NUMBER() OVER(ORDER BY A.DEPT_CODE DESC) AS TID,A.TYPE_ID,A.TYPE,A.BILL_TYPE,A.CASE_NO,A.MR_NO,"
					+ "A.PAT_NAME,A.DEPT_CODE,A.DEPT_DESC,A.DR_CODE,"
					+ "A.DR_DESC,A.EXE_DEPT_CODE,A.EXE_DEPT_DESC,"
					+ "A.CLINICAREA_CODE,A.CLINIC_DESC,A.PRINT_NAME,"
					+ "A.PRINT_NO,A.TOT_AMT,A.ACCOUNT_DATE,A.BILL_DATE,A.PRINT_USER,A.INS_FLG,A.BILL_ID "
					+ ",A.UP_LOAD,'δ���' LOAD_DOWN  "
					+" FROM (SELECT '001' AS TYPE_ID,'����' AS TYPE,"
					+ "C.REXP_CODE AS BILL_ID,A.CASE_NO,A.MR_NO,D.PAT_NAME,A.DEPT_CODE,M.DEPT_CHN_DESC AS DEPT_DESC,A.DR_CODE,"
					+ "H.USER_NAME AS DR_DESC,C.EXEC_DEPT_CODE AS EXE_DEPT_CODE,E.DEPT_CHN_DESC AS EXE_DEPT_DESC,"
					+ "F.CLINICAREA_CODE,G.CLINIC_DESC,'' PRINT_NAME,"
					+ "C.RECEIPT_NO PRINT_NO,SUM(C.AR_AMT) AS TOT_AMT,Q.ACCOUNT_SEQ," 
					+ "#"
//					+ "MAX(C.EXEC_DATE) AS ACCOUNT_DATE,MAX(C.EXEC_DATE) AS BILL_DATE,"
					+ "Q.CASHIER_CODE AS PRINT_USER, 'N' INS_FLG,"
					+ "N.CHN_DESC AS BILL_TYPE,CASE WHEN C.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD "
					+ "FROM REG_PATADM A,SYS_PATINFO D,SYS_DEPT E,REG_CLINICROOM F,"
					+ "REG_CLINICAREA G,SYS_OPERATOR H,OPD_ORDER C,SYS_DEPT M,SYS_DICTIONARY N,BIL_OPB_RECP Q  "
					+ "WHERE C.REXP_CODE=N.ID AND N.GROUP_ID='SYS_CHARGE' "
					+ "AND A.CASE_NO=C.CASE_NO AND A.MR_NO=D.MR_NO AND C.CASE_NO=Q.CASE_NO(+) AND C.RECEIPT_NO=Q.RECEIPT_NO(+) "
					+ "AND C.EXEC_DEPT_CODE=E.DEPT_CODE(+) AND A.CLINICROOM_NO=F.CLINICROOM_NO(+) AND C.AR_AMT<>0 "
					+ "AND A.DEPT_CODE=M.DEPT_CODE(+) "
					+ "AND F.CLINICAREA_CODE=G.CLINICAREA_CODE(+) AND A.DR_CODE=H.USER_ID "
					+ "AND A.REGCAN_USER IS NULL ";
			dateName = " AND C.EXEC_DATE BETWEEN TO_DATE('"
					+ parm.getValue("START_DATE")
					+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
					+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') AND C.EXEC_FLG='Y' ";
			if (null!=parm.getValue("MR_NO")&&parm.getValue("MR_NO").length()>0) {
				reDateName+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
				historyWhere+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
			}
			if (null!=parm.getValue("REXP_CODE")&&parm.getValue("REXP_CODE").length()>0) {
				reDateName+=" AND C.REXP_CODE='"+parm.getValue("REXP_CODE")+"'";
				historyWhere+=" AND C.REXP_CODE='"+parm.getValue("REXP_CODE")+"'";
			}
			if (null!=parm.getValue("OPD_TYPE")&&parm.getValue("OPD_TYPE").length()>0) {
				if (parm.getValue("OPD_TYPE").equals("2")) {//����
					reDateName+=" AND A.ADM_TYPE='O' ";
					historyWhere+=" AND A.ADM_TYPE='O' ";
				}else if(parm.getValue("OPD_TYPE").equals("3")){
					reDateName+=" AND A.ADM_TYPE='E' ";
					historyWhere+=" AND A.ADM_TYPE='E' ";
				}
			}
			group = "  GROUP BY C.REXP_CODE,A.DEPT_CODE,C.EXEC_DEPT_CODE,E.DEPT_CHN_DESC,M.DEPT_CHN_DESC,A.CASE_NO,A.MR_NO,D.PAT_NAME,A.DR_CODE, H.USER_NAME,"
				+ " F.CLINICAREA_CODE,G.CLINIC_DESC,A.ADM_TYPE,N.CHN_DESC,C.BIL_FINANCE_FLG,C.RECEIPT_NO,Q.ACCOUNT_SEQ,Q.CASHIER_CODE ";
			if (null!=parm.getValue("PRINT_TYPE")&&parm.getValue("PRINT_TYPE").length()>0) {
				if (parm.getValue("PRINT_TYPE").equals("2")) {//�Ѵ�Ʊ
					if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").equals("2")) {
					}else{
						
						String sqlr = "  SELECT B.CASE_NO,A.MR_NO,B.RECEIPT_NO,SUM (C.REDUCE_AMT) AMT," +
						" C.REXP_CODE FROM REG_PATADM A, BIL_OPB_RECP B, BIL_REDUCEM D, BIL_REDUCED C" +
						" WHERE     A.CASE_NO = B.CASE_NO AND A.REGCAN_USER IS NULL" +
//						"  AND B.RESET_RECEIPT_NO IS NULL AND B.TOT_AMT > 0" +
						reDateName +
						" AND B.RECEIPT_NO IS NOT NULL" +
						" AND B.ACCOUNT_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
						" AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS')" +
						" AND B.CASE_NO = D.CASE_NO AND B.REDUCE_NO = D.REDUCE_NO" +
						" AND B.RECEIPT_NO = D.BUSINESS_NO" +
						" AND C.REDUCE_NO = D.REDUCE_NO" +
						" GROUP BY B.CASE_NO,A.MR_NO, B.RECEIPT_NO,C.REXP_CODE "
						+ "union all  SELECT B.CASE_NO, A.MR_NO,B.RECEIPT_NO, SUM (-C.REDUCE_AMT) AMT,C.REXP_CODE "+
						"FROM REG_PATADM A,   BIL_OPB_RECP B, BIL_REDUCEM D, BIL_REDUCED C,"
						+ "(select receipt_no from bil_opb_recp where ACCOUNT_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
						" AND TO_DATE ('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS')) e "
						+"WHERE     A.CASE_NO = B.CASE_NO "+reDateName 
						+ "AND A.REGCAN_USER IS NULL and B.RESET_RECEIPT_NO = e.receipt_no"
						+ " AND B.RESET_RECEIPT_NO IS NOT NULL AND B.CASE_NO = D.CASE_NO AND "
						+ "B.REDUCE_NO = D.REDUCE_NO  AND B.RECEIPT_NO = D.BUSINESS_NO AND C.REDUCE_NO = D.REDUCE_NO "
						+ "GROUP BY B.CASE_NO, A.MR_NO, B.RECEIPT_NO, C.REXP_CODE";
						System.out.println("����SQL===="+sqlr); 
						parmRe = new TParm(TJDODBTool.getInstance().select(sqlr));
						
					}
					reDateName+=" AND C.RECEIPT_NO IS NOT NULL AND Q.ACCOUNT_DATE BETWEEN TO_DATE('"
						+ parm.getValue("START_DATE")
						+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
						+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') ";

					dateName="";
//					historyWhere+= " AND C.OPT_DATE BETWEEN TO_DATE('"
//						+ parm.getValue("START_DATE")
//						+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
//						+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') ";
//					historyWhere+= " AND C.DC_ORDER_DATE BETWEEN "
//						+ parm.getValue("START_DATE")
//						+ " AND "
//						+ parm.getValue("END_DATE") + " ";
					String [] whereArray={"A.RESET_RECEIPT_NO = B.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL "," A.RECEIPT_NO = B.RECEIPT_NO AND A.RESET_RECEIPT_NO IS NOT NULL "};
					String [] arArray={"-SUM (C.AR_AMT)","SUM (C.AR_AMT)"};
					String [] whereArrayTwo={"TOT_AMT<0","RESET_RECEIPT_NO IS NOT NULL "};// AND MEM_PACK_FLG ='N' 
					if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").length()>0 &&
							!parm.getValue("MEM_TYPE").equals("1")) {
						if (parm.getValue("MEM_TYPE").equals("2")) {//����
							historyName+=" AND MEM_PACK_FLG ='Y' AND TOT_AMT <>0 ";
						}else if(parm.getValue("MEM_TYPE").equals("3")){//����
							historyName+=" AND MEM_PACK_FLG ='N'  ";
						}
					}
					for (int i = 0; i < arArray.length; i++) {
						reSql+="  UNION ALL SELECT '001' AS TYPE_ID, '����' AS TYPE, C.REXP_CODE AS BILL_ID,"+
		                 "A.CASE_NO, A.MR_NO, D.PAT_NAME,A.DEPT_CODE,M.DEPT_CHN_DESC AS DEPT_DESC,"+
		                 "A.DR_CODE, H.USER_NAME AS DR_DESC,C.EXEC_DEPT_CODE AS EXE_DEPT_CODE,E.DEPT_CHN_DESC AS EXE_DEPT_DESC,"+
		                 "F.CLINICAREA_CODE,G.CLINIC_DESC, '' PRINT_NAME, C.RECEIPT_NO PRINT_NO,"+arArray[i]+" AS TOT_AMT,"+
		                 "Q.ACCOUNT_SEQ," +
		                 "MAX (Q.ACCOUNT_DATE) AS ACCOUNT_DATE," +
		                 " MAX (Q.ACCOUNT_DATE) AS BILL_DATE, " +
		                 "Q.CASHIER_CODE AS PRINT_USER,"+
		                 "'N' INS_FLG,N.CHN_DESC AS BILL_TYPE,CASE WHEN C.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD "+
		                 " FROM REG_PATADM A, SYS_PATINFO D,SYS_DEPT E, REG_CLINICROOM F, REG_CLINICAREA G,"+
		                 "SYS_OPERATOR H,OPD_ORDER_HISTORY C, SYS_DEPT M, SYS_DICTIONARY N," +
		                 "(SELECT A.RECEIPT_NO, A.CASE_NO,A.MR_NO, B.ACCOUNT_DATE_SUM AS ACCOUNT_DATE, B.ACCOUNT_SEQ, B.CASHIER_CODE FROM BIL_OPB_RECP A," +
		                 " (SELECT RECEIPT_NO,ACCOUNT_SEQ,TO_CHAR(ACCOUNT_DATE,'YYYYMMDD') ACCOUNT_DATE,ACCOUNT_DATE AS ACCOUNT_DATE_SUM,CASHIER_CODE FROM BIL_OPB_RECP" +
		                 " WHERE ACCOUNT_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS')" +
		                 " AND TO_DATE ('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') "+historyName+"  AND "+whereArrayTwo[i]+") B  " +
		                 " WHERE  "+whereArray[i]+historyName+") Q" +
		               //add by huangtt 20151027 end
		            " WHERE     C.REXP_CODE = N.ID AND N.GROUP_ID = 'SYS_CHARGE' "+
		                " AND A.CASE_NO = C.CASE_NO AND A.MR_NO = D.MR_NO AND C.CASE_NO = Q.CASE_NO(+)"+
		                " AND C.RECEIPT_NO = Q.RECEIPT_NO(+) AND C.EXEC_DEPT_CODE = E.DEPT_CODE(+) "+
		                " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+) AND C.AR_AMT <> 0 "+
		                " AND A.DEPT_CODE = M.DEPT_CODE(+) AND F.CLINICAREA_CODE = G.CLINICAREA_CODE(+) "+
		                " AND A.DR_CODE = H.USER_ID  AND A.REGCAN_USER IS NULL and C.RECEIPT_NO IS NOT NULL "+historyWhere+
		                " AND C.RECEIPT_NO = Q.RECEIPT_NO "+group;
					}
//		                +" AND Q.CHARGE_DATE> TO_DATE('"
//					+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') ";
					//System.out.println("reSql:::::"+reSql);
				}else if(parm.getValue("PRINT_TYPE").equals("3")){
					reDateName+=" AND C.RECEIPT_NO IS NULL ";
				}
			}else{
				historyWhere+= " AND C.DC_ORDER_DATE BETWEEN "
					+ parm.getValue("START_DATE")
					+ " AND "
					+ parm.getValue("END_DATE") + " ";
				reSql="  UNION ALL SELECT '001' AS TYPE_ID, '����' AS TYPE, C.REXP_CODE AS BILL_ID,"+
	                 "A.CASE_NO, A.MR_NO, D.PAT_NAME,A.DEPT_CODE,M.DEPT_CHN_DESC AS DEPT_DESC,"+
	                 "A.DR_CODE, H.USER_NAME AS DR_DESC,C.EXEC_DEPT_CODE AS EXE_DEPT_CODE,E.DEPT_CHN_DESC AS EXE_DEPT_DESC,"+
	                 "F.CLINICAREA_CODE,G.CLINIC_DESC, '' PRINT_NAME, C.RECEIPT_NO PRINT_NO, -SUM (C.AR_AMT) AS TOT_AMT,"+
	                 "Q.ACCOUNT_SEQ,MAX (C.OPT_DATE) AS ACCOUNT_DATE, MAX (C.OPT_DATE) AS BILL_DATE, Q.CASHIER_CODE AS PRINT_USER,"+
	                 "'N' INS_FLG,N.CHN_DESC AS BILL_TYPE,CASE WHEN C.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD "+
	                 " FROM REG_PATADM A, SYS_PATINFO D,SYS_DEPT E, REG_CLINICROOM F, REG_CLINICAREA G,"+
	                 "SYS_OPERATOR H,OPD_ORDER_HISTORY C, SYS_DEPT M, SYS_DICTIONARY N, BIL_OPB_RECP Q"+
	          " WHERE     C.REXP_CODE = N.ID AND N.GROUP_ID = 'SYS_CHARGE' "+
	                " AND A.CASE_NO = C.CASE_NO AND A.MR_NO = D.MR_NO AND C.CASE_NO = Q.CASE_NO(+)"+
	                " AND C.RECEIPT_NO = Q.RECEIPT_NO(+) AND C.EXEC_DEPT_CODE = E.DEPT_CODE(+) "+
	                " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+) AND C.AR_AMT <> 0 "+
	                " AND A.DEPT_CODE = M.DEPT_CODE(+) AND F.CLINICAREA_CODE = G.CLINICAREA_CODE(+) "+
	                " AND A.DR_CODE = H.USER_ID  AND A.REGCAN_USER IS NULL and c.receipt_no is not null "+historyWhere+group;
			}
			if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").length()>0 &&
					!parm.getValue("MEM_TYPE").equals("1")) {
				reDateName+=" AND C.RECEIPT_NO IS NOT NULL AND Q.ACCOUNT_DATE BETWEEN TO_DATE('"
					+ parm.getValue("START_DATE")
					+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
					+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') ";
				if (parm.getValue("MEM_TYPE").equals("3")) {//����
					reDateName+=" AND Q.MEM_PACK_FLG ='N'  ";
					//historyName+=" AND Q.MEM_PACK_FLG ='N'  ";
				}else if(parm.getValue("MEM_TYPE").equals("2")){//����
					reDateName+=" AND Q.MEM_PACK_FLG ='Y' AND Q.TOT_AMT <>0 ";
					//historyName+=" AND Q.MEM_PACK_FLG ='N'  ";
				}
			}
			
			    	//start------add by huangtt 20150922 ����Ǹ���account_dateʱ���ѯ�Ļ���ACCOUNT_DATE,BILL_DATEȡbil_opb_recp���е��ֶ�
			if(dateName.length()>0){
	    		sql = sql.replaceFirst("#", "MAX(C.EXEC_DATE) AS ACCOUNT_DATE,MAX(C.EXEC_DATE) AS BILL_DATE,");
	    		group2="";
	    	}else{
	    		sql = sql.replaceFirst("#", "Q.ACCOUNT_DATE,Q.BILL_DATE,");
	    		group2=",Q.ACCOUNT_DATE,Q.BILL_DATE";
	    	}
			//end------add by huangtt 20150922
			//===start===add by kangy 20171120 �����ʹ���ײ���δʹ�õ�ҽ������
			String memWhere=" AND Q.ACCOUNT_DATE BETWEEN TO_DATE('"
					+ parm.getValue("START_DATE")
					+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
					+ parm.getValue("END_DATE") + "','YYYYMMDDHH24MISS') ";
			String opbWhere="";
			if (null!=parm.getValue("MR_NO")&&parm.getValue("MR_NO").length()>0) {
				memWhere=" AND Q.MR_NO='"+parm.getValue("MR_NO")+"' ";
			}
			if (null!=parm.getValue("MEM_TYPE")&&parm.getValue("MEM_TYPE").length()>0 &&
					!parm.getValue("MEM_TYPE").equals("1")) {
				if (parm.getValue("MEM_TYPE").equals("2")) {//����
					opbWhere=" AND B.MEM_PACK_FLG ='Y' AND TOT_AMT <>0 ";
				}else if(parm.getValue("MEM_TYPE").equals("3")){//����
					opbWhere+=" AND B.MEM_PACK_FLG ='N'  ";
				}
			}
			String memSql="";
			if ("".equals(parm.getValue("OPD_TYPE"))||"2".equals(parm.getValue("OPD_TYPE"))||"1".equals(parm.getValue("OPD_TYPE"))) {
			 memSql=" UNION ALL SELECT '001' AS TYPE_ID, '����' AS TYPE, C.REXP_CODE AS BILL_ID, '' CASE_NO, C.MR_NO, D.PAT_NAME,'011100' DEPT_CODE, '��ƺ��㲿' DEPT_DESC,'' DR_CODE, "+
                  " '' DR_DESC,'011100' EXE_DEPT_CODE, '��ƺ��㲿' EXE_DEPT_DESC, '' CLINICAREA_CODE, ''CLINIC_DESC, '' PRINT_NAME, C.RECEIPT_NO PRINT_NO, C.ARAMT TOT_AMT,"+
                     " Q.ACCOUNT_SEQ, Q.ACCOUNT_DATE,Q.BILL_DATE, Q.CASHIER_CODE AS PRINT_USER, 'N' INS_FLG, N.CHN_DESC AS BILL_TYPE, CASE WHEN C.BIL_FINANCE_FLG = 'Y' THEN '���ϴ�' "+
                    "  ELSE 'δ�ϴ�' END UP_LOAD "+
                         " FROM SYS_PATINFO D, SYS_OPERATOR H,   BIL_MEM_RECP Q, SYS_DICTIONARY N, (SELECT MR_NO,   REXP_CODE, ARAMT, BIL_FINANCE_FLG,  RECEIPT_NO, TRADE_NO "+
                      " FROM BIL_MEM_RECP UNPIVOT (ARAMT FOR REXP_CODE IN  (CHARGE01 AS '114.1',  CHARGE02 AS '114.2', CHARGE03 AS '115', CHARGE04 AS '116', CHARGE05 AS '107', CHARGE06 AS '109',CHARGE07 AS '103', "+
                        "  CHARGE08 AS '112',CHARGE09 AS '111',CHARGE10 AS '108', CHARGE11 AS '101', CHARGE12 AS '102',  CHARGE13 AS '118', CHARGE14 AS '104', CHARGE15 AS '105',"+
                         "   CHARGE16 AS '106', CHARGE17 AS '113', CHARGE18 AS '110',CHARGE19 AS '117',  CHARGE20 AS '119'))  WHERE ARAMT <> 0) C    "+
                    "     WHERE   C.MR_NO = D.MR_NO AND Q.CASHIER_CODE = H.USER_ID(+)  AND C.RECEIPT_NO = Q.RECEIPT_NO(+) AND N.GROUP_ID = 'SYS_CHARGE' AND C.REXP_CODE = N.ID  AND C.RECEIPT_NO IS NOT NULL "+memWhere;
			}
			//===end===add by kangy 20171120 �����ʹ���ײ���δʹ�õ�ҽ������
			sql+=dateName+reDateName+group+group2+reSql+memSql+") A WHERE A.TOT_AMT<>0";		
			break;
		case 2://סԺ
			sql="SELECT ROW_NUMBER() OVER(ORDER BY A.CASE_NO DESC) AS TID,A.* FROM( SELECT TYPE_ID,TYPE,CASE_NO,IPD_NO,MR_NO,PAT_NAME,DEPT_CODE,DEPT_DESC,DR_CODE,BILL_ID,BILL_TYPE,DR_DESC,EXE_DEPT_CODE,EXE_DEPT_DESC,PRINT_NO,"+
            "TOT_AMT,ACCOUNT_SEQ,BILL_DATE,ACCOUNT_DATE,PRINT_USER,INS_FLG,CLINICAREA_CODE,CLINIC_DESC,PRINT_NAME,UP_LOAD,'δ���' LOAD_DOWN   FROM " +
            "(SELECT '002' AS TYPE_ID,'סԺ' AS TYPE ,A.CASE_NO,A.IPD_NO,A.MR_NO,D.PAT_NAME,"+
            " L.DEPT_CODE,E.DEPT_CHN_DESC AS DEPT_DESC,A.VS_DR_CODE AS DR_CODE,L.REXP_CODE AS BILL_ID,"+
            " H.USER_NAME AS DR_DESC ,L.EXE_DEPT_CODE,F.DEPT_CHN_DESC AS EXE_DEPT_DESC,"+
            " '' AS PRINT_NO,SUM(L.TOT_AMT) AS TOT_AMT,'' ACCOUNT_SEQ,MAX(L.BILL_DATE) AS BILL_DATE,MAX(L.BILL_DATE) AS ACCOUNT_DATE,"+
            " '' AS PRINT_USER,"+
            " CASE WHEN A.CONFIRM_NO IS NOT NULL THEN 'Y' ELSE 'N' END INS_FLG,L.STATION_CODE AS CLINICAREA_CODE,"+
            " S.STATION_DESC AS CLINIC_DESC,"+
            " '' AS PRINT_NAME, "+
            "N.CHN_DESC AS BILL_TYPE,CASE WHEN L.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD "+
            " FROM ADM_INP A ,SYS_PATINFO D,SYS_DEPT E,SYS_OPERATOR H, "+
            " IBS_ORDM K,IBS_ORDD L,SYS_STATION S,SYS_DEPT F,SYS_DICTIONARY N "+
            " WHERE A.CASE_NO=K.CASE_NO AND A.CASE_NO=L.CASE_NO AND K.CASE_NO=L.CASE_NO AND K.CASE_NO_SEQ=L.CASE_NO_SEQ"+
            " AND L.STATION_CODE = S.STATION_CODE(+) AND L.REXP_CODE=N.ID AND N.GROUP_ID='SYS_CHARGE' "+
            " AND A.MR_NO=D.MR_NO AND L.DEPT_CODE=E.DEPT_CODE(+) AND L.EXE_DEPT_CODE=F.DEPT_CODE(+) "+
            " AND L.DR_CODE=H.USER_ID(+) ";
		group=" GROUP BY L.REXP_CODE,A.CASE_NO,A.IPD_NO,A.MR_NO,D.PAT_NAME,L.DEPT_CODE,E.DEPT_CHN_DESC,L.EXE_DEPT_CODE,F.DEPT_CHN_DESC,A.VS_DR_CODE,A.CONFIRM_NO,S.STATION_DESC,"+
                " H.USER_NAME,L.STATION_CODE,N.CHN_DESC,L.BIL_FINANCE_FLG ";
		String whereR="";
		dateName=" AND L.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+
		"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') ";
		if (null!=parm.getValue("MR_NO")&&parm.getValue("MR_NO").length()>0) {
			dateName+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
			whereR+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
		}
		if (null!=parm.getValue("REXP_CODE")&&parm.getValue("REXP_CODE").length()>0) {
			dateName+=" AND L.REXP_CODE='"+parm.getValue("REXP_CODE")+"'";
			whereR+=" AND L.REXP_CODE='"+parm.getValue("REXP_CODE")+"'";
		}
//		if (null!=parm.getValue("PRINT_TYPE")&&parm.getValue("PRINT_TYPE").length()>0) {
//			if (parm.getValue("PRINT_TYPE").equals("2")) {//�Ѵ�Ʊ
//				dateName+=" AND C.RECEIPT_NO IS NOT NULL ";
//			}else if(parm.getValue("PRINT_TYPE").equals("3")){
//				dateName+=" AND C.RECEIPT_NO IS NULL ";
//			}
//		}	
		String reduceSql=" UNION ALL  SELECT '002' AS TYPE_ID,'סԺ' AS TYPE ," +
				"A.CASE_NO, A.IPD_NO,A.MR_NO,D.PAT_NAME,A.DEPT_CODE,E.DEPT_CHN_DESC AS DEPT_DESC," +
				"A.VS_DR_CODE AS DR_CODE,L.REXP_CODE AS BILL_ID,"+
            " H.USER_NAME AS DR_DESC ,A.DEPT_CODE AS EXE_DEPT_CODE,E.DEPT_CHN_DESC AS EXE_DEPT_DESC,S.PRINT_NO,"
				+ "CASE WHEN  S.AR_AMT>0  THEN -SUM (L.REDUCE_AMT) ELSE SUM(L.REDUCE_AMT) END AS TOT_AMT,S.ACCOUNT_SEQ,MAX(S.CHARGE_DATE) AS BILL_DATE ,MAX(S.ACCOUNT_DATE) AS ACCOUNT_DATE,'' AS PRINT_USER," 
				+ "CASE WHEN A.CONFIRM_NO IS NOT NULL THEN 'Y' ELSE 'N' END INS_FLG,A.STATION_CODE AS CLINICAREA_CODE,"+
				" T.STATION_DESC AS CLINIC_DESC, '' AS PRINT_NAME, "+
				"N.CHN_DESC AS BILL_TYPE,'' UP_LOAD"  +
					" FROM ADM_INP A,SYS_PATINFO D,SYS_DICTIONARY N," +
					"BIL_REDUCEM K,BIL_REDUCED L, BIL_IBS_RECPM S,SYS_DEPT E,SYS_OPERATOR H,SYS_STATION T "+
					"WHERE A.CASE_NO=K.CASE_NO AND A.CASE_NO=S.CASE_NO " +
					"AND A.STATION_CODE = T.STATION_CODE(+) AND L.REXP_CODE=N.ID AND N.GROUP_ID='SYS_CHARGE'" +
					" AND A.MR_NO = D.MR_NO AND A.DEPT_CODE = E.DEPT_CODE(+) " +
					" AND K.REDUCE_NO=L.REDUCE_NO AND S.REDUCE_NO=K.REDUCE_NO AND S.CASE_NO=K.CASE_NO AND K.ADM_TYPE='I' " +
					" AND A.VS_DR_CODE=H.USER_ID(+)" +
					" AND S.ACCOUNT_DATE BETWEEN TO_DATE('"+parm.getValue("START_DATE")+
					"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"','YYYYMMDDHH24MISS') "+whereR
					+" GROUP BY A.MR_NO, A.IPD_NO,D.PAT_NAME,A.CASE_NO,H.USER_NAME,S.ACCOUNT_SEQ,A.CONFIRM_NO,S.AR_AMT, "+
             "A.DEPT_CODE,L.REXP_CODE,E.DEPT_CHN_DESC,T.STATION_DESC,A.STATION_CODE,N.CHN_DESC,A.VS_DR_CODE,S.PRINT_NO ";
		sql+=dateName+group+reduceSql+")WHERE TOT_AMT<>0  ORDER BY CASE_NO ) A ORDER BY TID";
		break;

		default:
			break;
		}
    	
    	System.out.println("SQL::Ӧ������:::new:"+sql);
    	parm=new TParm(TJDODBTool.getInstance().select(sql));   	
    	//����ѯ�����ļ�������뵽parm�� add by huangtt 0151009
    	if(parmRe.getCount() > 0){
    		TParm parmR = new TParm();
    		for (int i = 0; i < parmRe.getCount(); i++) { 
    			boolean flg = false;
    			for (int j = 0; j < parm.getCount(); j++) {
    				if(parmRe.getValue("CASE_NO", i).equals(parm.getValue("CASE_NO", j)) &&
    						parmRe.getValue("MR_NO", i).equals(parm.getValue("MR_NO", j)) &&
    						parmRe.getValue("RECEIPT_NO", i).equals(parm.getValue("PRINT_NO", j)) &&
    						parmRe.getValue("REXP_CODE", i).equals(parm.getValue("BILL_ID", j))){
    					parmR.addRowData(parm, j);
    					flg = true;
    					break;
    					
    				}
    			}
    			if(flg){
    				int row = parmR.getCount("CASE_NO");
        			parmR.setData("TOT_AMT", row-1, -parmRe.getDouble("AMT", i));
    			}
    			
    			
    		}
    		for (int i = 0; i < parmR.getCount("CASE_NO"); i++) {
				parm.addRowData(parmR, i);
			}
    		parm.setCount(parm.getCount("CASE_NO"));
    	}
    	parm.setData("REDUCE_AMT",reduceAmt);
    	return parm;
    }
    /**
     * Ԥ�������ݲ�ѯ
     * @param parm
     * @return
     */
	public TParm onQueryByTypePay(TParm parm) {
		int type = parm.getInt("TYPE");
		String sql = "";
		String dateName="";
		String dateName2="";
		String group="";
		String where="";
		String payTypewhere="";
		String mrNowhere="";
		if (null!=parm.getValue("MR_NO")&&parm.getValue("MR_NO").length()>0) {
			where+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
			mrNowhere+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"' ";
		//	reDateName+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
		}
		if (null!=parm.getValue("PAY_TYPE")&&parm.getValue("PAY_TYPE").length()>0) {
			if (type==1) {
				where+=" AND A.GATHER_TYPE='"+parm.getValue("PAY_TYPE")+"'";
				payTypewhere+=" AND A.GATHER_TYPE='"+parm.getValue("PAY_TYPE")+"'";
			}else{
				where+=" AND A.PAY_TYPE='"+parm.getValue("PAY_TYPE")+"'";
			}
			
			
		//	reDateName+=" AND C.REXP_CODE='"+parm.getValue("REXP_CODE")+"'";
		}
		switch (type) {
		case 1:
			dateName=" AND A.STORE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
			dateName2=" AND A.ACCOUNT_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
			/*sql="SELECT ROW_NUMBER() OVER(ORDER BY A.BIL_BUSINESS_NO DESC) AS TID,CASE WHEN A.ACCNT_TYPE='6' THEN -A.AMT ELSE A.AMT END AS TOT_AMT,A.STORE_DATE AS BILL_DATE,D.USER_NAME AS PRINT_NAME," +
			"A.MR_NO,E.PAT_NAME,'����' AS  TYPE, '001' AS TYPE_ID,A.CREAT_USER AS PRINT_USER,A.STORE_DATE AS ACCOUNT_DATE,A.BIL_BUSINESS_NO AS RECEIPT_NO, " +
			"G.CHN_DESC AS BILL_TYPE, "+
			"A.GATHER_TYPE AS BILL_ID "+
			"FROM EKT_BIL_PAY A,SYS_OPERATOR D,SYS_PATINFO E ,SYS_DICTIONARY G WHERE " +
			" A.CREAT_USER=D.USER_ID AND A.GATHER_TYPE=G.ID AND G.GROUP_ID='GATHER_TYPE' AND A.MR_NO=E.MR_NO " +
			" AND A.ACCNT_TYPE IN('4','6')"+dateName +" ORDER BY MR_NO";*/
			
			// modify caoyong ������� 2014��5��19
			sql=" SELECT   ROW_NUMBER () OVER (ORDER BY A.RECEIPT_NO DESC) AS TID,A.TOT_AMT,A.BILL_DATE,A.PRINT_NAME, "+
                "A.MR_NO, A.PAT_NAME,A.TYPE,A.TYPE_ID,A.PRINT_USER,A.ACCOUNT_DATE,A.RECEIPT_NO,A.BILL_TYPE,A.BILL_ID,A.UP_LOAD,A.LOAD_DOWN " +
                ",A.PACKAGE_CODE,A.PACKAGE_DESC, A.CARD_TYPE," +
				"A.MEMO1, A.MEMO2,A.MEMO3,A.MEMO4,A.MEMO5,A.MEMO6,A.MEMO7,A.MEMO8,A.MEMO9,A.MEMO10 "+// add by kangy 20170712 ��ӿ�����
	            "FROM (SELECT CASE WHEN A.ACCNT_TYPE='6' THEN -A.AMT ELSE A.AMT END AS TOT_AMT,A.STORE_DATE AS BILL_DATE,D.USER_NAME AS PRINT_NAME," +
				"A.MR_NO,E.PAT_NAME,'ҽ�ƿ�' AS  TYPE, '001' AS TYPE_ID,A.CREAT_USER AS PRINT_USER,A.STORE_DATE AS ACCOUNT_DATE,A.BIL_BUSINESS_NO AS RECEIPT_NO, " +
				"A.GATHER_TYPE AS BILL_ID," +
				"G.CHN_DESC AS BILL_TYPE, 'EKT' AS PACKAGE_CODE , 'ҽ�ƿ�' AS PACKAGE_DESC, "+
				"CASE WHEN A.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN,H.CHN_DESC CARD_TYPE," +
				"TO_NCHAR('') MEMO1, TO_NCHAR('') MEMO2,TO_NCHAR('') MEMO3,TO_NCHAR('') MEMO4,TO_NCHAR('') MEMO5,TO_NCHAR('') MEMO6,TO_NCHAR('') MEMO7,TO_NCHAR('') MEMO8,TO_NCHAR('') MEMO9,TO_NCHAR('') MEMO10  "+
				"FROM EKT_BIL_PAY A LEFT JOIN SYS_DICTIONARY H  ON (A.CARD_TYPE=H.ID AND H.GROUP_ID='SYS_CARDTYPE')," +//add by kangy 20170712 ��ӿ����ͣ����ģ�
				"SYS_OPERATOR D,SYS_PATINFO E ,SYS_DICTIONARY G WHERE " +
				" A.CREAT_USER=D.USER_ID AND A.GATHER_TYPE=G.ID AND G.GROUP_ID='GATHER_TYPE' AND A.MR_NO=E.MR_NO " +
				" AND A.ACCNT_TYPE IN('4','6')"+dateName+where ;
			
			//�ײ�ԤԼ��
			String payTypeSql = "SELECT A.GATHER_TYPE, B.CHN_DESC, A.PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE A, SYS_DICTIONARY B" +
					" WHERE A.GATHER_TYPE = B.ID AND B.GROUP_ID = 'GATHER_TYPE' "+payTypewhere;
			TParm payTypeParm = new TParm(TJDODBTool.getInstance().select(payTypeSql));
			String [] arArray={"D.REST_TRADE_NO","D.TRADE_NO"};
			
			for (int i = 0; i < payTypeParm.getCount(); i++) {
				String payType = payTypeParm.getValue("PAYTYPE", i);
				if(payType.equals("PAY_TYPE11")){
					payType = payType + "+A.PAY_MEDICAL_CARD";
				}
				for (int j = 0; j < arArray.length; j++) {
					sql += "  UNION ALL SELECT DISTINCT A."+payType+" AS TOT_AMT, A.BILL_DATE, C.USER_NAME AS PRINT_NAME, A.MR_NO, B.PAT_NAME," +
							" 'Ԥ���ײ�' AS TYPE,'001' AS TYPE_ID,A.CASHIER_CODE AS PRINT_USER, A.ACCOUNT_DATE,'' AS RECEIPT_NO," +
							" '"+payTypeParm.getValue("GATHER_TYPE", i)+"' AS BILL_ID," +
							" '"+payTypeParm.getValue("CHN_DESC", i)+"' AS BILL_TYPE, " +
							" D.PACKAGE_CODE, D.PACKAGE_DESC, CASE WHEN A.BIL_FINANCE_FLG = 'Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD," +
							" 'δ���' LOAD_DOWN,'' CARD_TYPE, A.MEMO1,A.MEMO2,A.MEMO3,A.MEMO4,A.MEMO5,A.MEMO6,A.MEMO7,A.MEMO8,A.MEMO9,A.MEMO10 " +//add by kangy 20170712
							"FROM MEM_PACKAGE_TRADE_M A, SYS_PATINFO B, SYS_OPERATOR C, MEM_PAT_PACKAGE_SECTION D" +
							" WHERE  A.MR_NO = B.MR_NO AND A.CASHIER_CODE = C.USER_ID" +
							" AND A.TRADE_NO = " +arArray[j]+
							" AND A."+payType+" <> 0" + dateName2 +mrNowhere;
//							+
//							" GROUP BY A.PAY_TYPE01, A.BILL_DATE, C.USER_NAME, A.MR_NO, B.PAT_NAME, A.CASHIER_CODE,A.ACCOUNT_DATE, " +
//							" D.PACKAGE_CODE, D.PACKAGE_DESC, A.BIL_FINANCE_FLG";
				}

				
			}
			//add by huangtt 20181212 ��Ա��
			for (int i = 0; i < payTypeParm.getCount(); i++) {
				String payType = payTypeParm.getValue("PAYTYPE", i);
				sql += "  UNION ALL SELECT DISTINCT A."+payType+" AS TOT_AMT, A.OPT_DATE AS BILL_DATE, C.USER_NAME AS PRINT_NAME, A.MR_NO, B.PAT_NAME," +
				" '��Ա��' AS TYPE,'001' AS TYPE_ID,A.OPT_USER AS PRINT_USER, A.ACCOUNT_DATE,'' AS RECEIPT_NO," +
				" '"+payTypeParm.getValue("GATHER_TYPE", i)+"' AS BILL_ID," +
				" '"+payTypeParm.getValue("CHN_DESC", i)+"' AS BILL_TYPE, " +
				" 'MEMCARD' AS PACKAGE_CODE, '��Ա��' AS PACKAGE_DESC, CASE WHEN A.BIL_FINANCE_FLG = 'Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD," +
				" 'δ���' LOAD_DOWN,'' CARD_TYPE, A.MEMO1,A.MEMO2,A.MEMO3,A.MEMO4,A.MEMO5,A.MEMO6,A.MEMO7,A.MEMO8,A.MEMO9,A.MEMO10 " +//add by kangy 20170712
				"FROM MEM_TRADE A, SYS_PATINFO B, SYS_OPERATOR C" +
				" WHERE  A.MR_NO = B.MR_NO AND A.OPT_USER = C.USER_ID" +
				" AND A."+payType+" <> 0" + dateName2 +mrNowhere;
			}
			
			sql += " ) A ";
			break;
			
		case 2:
			dateName=" AND A.CHARGE_DATE BETWEEN "+                           
			" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
			/*sql="SELECT ROW_NUMBER() OVER(ORDER BY A.CASE_NO DESC) AS TID,A.PRE_AMT AS TOT_AMT,A.CHARGE_DATE AS BILL_DATE,D.USER_NAME AS PRINT_NAME," +
				"B.CASE_NO,B.MR_NO,E.PAT_NAME,'סԺ' AS TYPE, '002' AS TYPE_ID,A.CASHIER_CODE AS PRINT_USER,A.CHARGE_DATE AS ACCOUNT_DATE,A.RECEIPT_NO, " +
				"F.CHN_DESC AS BILL_TYPE, "+
				"A.PAY_TYPE AS BILL_ID "+
				"FROM BIL_PAY A ,ADM_INP B,SYS_OPERATOR D,SYS_PATINFO E,SYS_DICTIONARY F WHERE A.CASE_NO=B.CASE_NO AND " +
				" A.CASHIER_CODE=D.USER_ID AND B.MR_NO=E.MR_NO AND B.CANCEL_FLG='N' AND F.GROUP_ID='GATHER_TYPE'" +
				" AND A.PAY_TYPE=F.ID AND A.TRANSACT_TYPE IN('01','04','02')"+dateName +"ORDER BY B.CASE_NO";*/
			//"GROUP BY A.REFUND_FLG,B.MR_NO,C.PAT_NAME,B.CASE_NO,A.RECEIPT_TYPE,A.CASHIER_CODE,A.CHARGE_DATE,A.RECEIPT_NO";
			
			// modify caoyong ������� 2014��5��19
			sql="SELECT ROW_NUMBER () OVER (ORDER BY A.CASE_NO DESC) AS TID,A.TOT_AMT,A.BILL_DATE, "+
                "A.PRINT_NAME,A.CASE_NO, A.MR_NO,A.PAT_NAME,A.TYPE,A.TYPE_ID,A.PRINT_USER, "+
                "A.ACCOUNT_DATE,A.RECEIPT_NO,A.BILL_TYPE,A.BILL_ID,A.UP_LOAD,A.LOAD_DOWN,'ODI' AS PACKAGE_CODE, 'סԺ' AS PACKAGE_DESC,CARD_TYPE," +
               	"'' MEMO1, '' MEMO2,'' MEMO3,'' MEMO4,'' MEMO5,'' MEMO6,'' MEMO7,'' MEMO8,'' MEMO9,'' MEMO10  "+
				"FROM(SELECT A.PRE_AMT AS TOT_AMT,A.CHARGE_DATE AS BILL_DATE,D.USER_NAME AS PRINT_NAME," +
				"B.CASE_NO,B.MR_NO,E.PAT_NAME,'סԺ' AS TYPE, '002' AS TYPE_ID,A.CASHIER_CODE AS PRINT_USER,A.CHARGE_DATE AS ACCOUNT_DATE,A.RECEIPT_NO, " +
				"F.CHN_DESC AS BILL_TYPE, "+
				"A.PAY_TYPE AS BILL_ID,CASE WHEN A.BIL_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN,H.CHN_DESC CARD_TYPE " +
				"FROM BIL_PAY A LEFT JOIN SYS_DICTIONARY H  ON (A.CARD_TYPE=H.ID AND H.GROUP_ID='SYS_CARDTYPE')," +//add by kangy ��ÿ����ͣ����ģ�
				"ADM_INP B,SYS_OPERATOR D,SYS_PATINFO E,SYS_DICTIONARY F WHERE A.CASE_NO=B.CASE_NO AND " +
				" A.CASHIER_CODE=D.USER_ID AND B.MR_NO=E.MR_NO " +
				//"AND B.CANCEL_FLG='N' " +//====pangben���ע�� ���ϵ�Ҳ��Ҫ��ѯ
				" AND F.GROUP_ID='GATHER_TYPE'" +
				" AND A.PAY_TYPE=F.ID AND A.TRANSACT_TYPE <>'03'"+dateName+where +"ORDER BY B.CASE_NO )A ";
			
			
			break;
		default:
			break;
		}
		System.out.println("SQL:Ԥ��������:::"+sql+group);
		parm=new TParm(TJDODBTool.getInstance().select(sql+group));
		return parm;
	}
	
	/**
	 * ��ֵ�Ĳ����ݲ�ѯ
	 * @param parm
	 * @return
	 */
	public TParm onQureyByBillDate(TParm parm){
		boolean isDebug = true;
		try {
				
			
			int type = parm.getInt("TYPE");
			String sql="";
			String dateName="";
			switch(type){
			case 1:
				dateName=" AND A.BILL_DATE BETWEEN "+                           
				" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') " ;
				sql="WITH B AS ( SELECT ROW_NUMBER () OVER (ORDER BY RECEIPT_NO DESC) AS TID,BILL_FLG,HRP_FLG,RECEIPT_NO,INV_CODE," +
						"ORDER_CODE,ORDER_DESC,REXP_CODE,REXP_DESC,CORP_CODE,AR_AMT,DOSAGE_QTY,ACCOUNT_DATE,BILL_USER," +
						"BILL_NAME,DEPT_CODE,DEPT_NAME,EXEC_DEPT_CODE,EXEC_DEPT_NAME,MR_NO,PAT_NAME,SEX_CODE,BIRTH_DATE,PAT_FAMLIY," +
						"PAT_TEL,DR_NAME,UP_LOAD,LOAD_DOWN,FLG" +
						" FROM (SELECT 'Y' AS BILL_FLG,'N' AS HRP_FLG,A.RECEIPT_NO,A.INV_CODE,A.ORDER_CODE,A.ORDER_DESC,REXP_CODE,"
					+" B.CHN_DESC AS REXP_DESC,'001' AS CORP_CODE,A.AR_AMT,A.DOSAGE_QTY,A.BILL_DATE AS ACCOUNT_DATE,"
					+" A.EXEC_DR_CODE BILL_USER,C.USER_NAME AS BILL_NAME,A.DEPT_CODE,D.DEPT_CHN_DESC AS DEPT_NAME,"
					+" A.EXEC_DEPT_CODE,E.COST_CENTER_CHN_DESC AS EXEC_DEPT_NAME,"
					+" A.MR_NO,F.PAT_NAME,G.CHN_DESC AS SEX_CODE,F.BIRTH_DATE,F.CONTACTS_NAME AS PAT_FAMLIY,"
					+" F.TEL_HOME AS PAT_TEL, H.USER_NAME AS DR_NAME,CASE WHEN A.BIL_HIGH_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN,'N' FLG  "
					+" FROM OPD_ORDER A,SYS_DICTIONARY B,SYS_OPERATOR C,SYS_DEPT D,SYS_COST_CENTER E,SYS_PATINFO F,SYS_DICTIONARY G,SYS_OPERATOR H,SYS_FEE I"
					+" WHERE B.GROUP_ID='SYS_CHARGE' AND B.ID=A.REXP_CODE"
					+" AND A.EXEC_DR_CODE=C.USER_ID(+)"//�շ�Ա
					+" AND A.DEPT_CODE=D.DEPT_CODE(+)"//��������
					+" AND A.MR_NO=F.MR_NO"
					+" AND G.GROUP_ID='SYS_SEX' AND G.ID=F.SEX_CODE"
					+" AND A.EXEC_DEPT_CODE=E.COST_CENTER_CODE(+)"
					+" AND A.DR_CODE=H.USER_ID(+)"
					+" AND A.ORDER_CODE=I.ORDER_CODE"
					+" AND I.SUPPLIES_TYPE='1' AND A.INV_CODE IS NOT NULL "
					+dateName+
					" ))" +
					",C AS (" +
					"  SELECT ROW_NUMBER () OVER (ORDER BY RECEIPT_NO DESC) AS TID,BILL_FLG,HRP_FLG,RECEIPT_NO,INV_CODE," +
						"ORDER_CODE,ORDER_DESC,REXP_CODE,REXP_DESC,CORP_CODE,AR_AMT,DOSAGE_QTY,ACCOUNT_DATE,BILL_USER," +
						"BILL_NAME,DEPT_CODE,DEPT_NAME,EXEC_DEPT_CODE,EXEC_DEPT_NAME,MR_NO,PAT_NAME,SEX_CODE,BIRTH_DATE,PAT_FAMLIY," +
						"PAT_TEL,DR_NAME,UP_LOAD,LOAD_DOWN,FLG" +
						" FROM" +
					"  (SELECT  A.BILL_FLG,'N' AS HRP_FLG,A.RECEIPT_NO,A.INV_CODE,A.ORDER_CODE,A.ORDER_DESC,REXP_CODE,"
					+" B.CHN_DESC AS REXP_DESC,'001' AS CORP_CODE,A.AR_AMT ,A.DOSAGE_QTY ,A.BILL_DATE AS ACCOUNT_DATE,"
					+" A.EXEC_DR_CODE BILL_USER,C.USER_NAME AS BILL_NAME,A.DEPT_CODE,D.DEPT_CHN_DESC AS DEPT_NAME,"
					+" A.EXEC_DEPT_CODE,E.COST_CENTER_CHN_DESC AS EXEC_DEPT_NAME,"
					+" A.MR_NO,F.PAT_NAME,G.CHN_DESC AS SEX_CODE,F.BIRTH_DATE,F.CONTACTS_NAME AS PAT_FAMLIY,"
					+" F.TEL_HOME AS PAT_TEL, H.USER_NAME AS DR_NAME,CASE WHEN A.BIL_HIGH_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN,'N' FLG  "
					+" FROM OPD_ORDER_HISTORY A,SYS_DICTIONARY B,SYS_OPERATOR C,SYS_DEPT D,SYS_COST_CENTER E,SYS_PATINFO F,SYS_DICTIONARY G,SYS_OPERATOR H,SYS_FEE I"
					+" WHERE B.GROUP_ID='SYS_CHARGE' AND B.ID=A.REXP_CODE"
					+" AND A.EXEC_DR_CODE=C.USER_ID(+)"//�շ�Ա
					+" AND A.DEPT_CODE=D.DEPT_CODE(+)"//��������
					+" AND A.MR_NO=F.MR_NO"
					+" AND G.GROUP_ID='SYS_SEX' AND G.ID=F.SEX_CODE"
					+" AND A.EXEC_DEPT_CODE=E.COST_CENTER_CODE(+)"
					+" AND A.DR_CODE=H.USER_ID(+)"
					+" AND A.ORDER_CODE=I.ORDER_CODE"
					+" AND I.SUPPLIES_TYPE='1' AND A.INV_CODE IS NOT NULL "
					+dateName+
					"  )) " +
					",D AS (" +
					"  SELECT ROW_NUMBER () OVER (ORDER BY RECEIPT_NO DESC) AS TID,BILL_FLG,HRP_FLG,RECEIPT_NO,INV_CODE," +
						"ORDER_CODE,ORDER_DESC,REXP_CODE,REXP_DESC,CORP_CODE,AR_AMT,DOSAGE_QTY,ACCOUNT_DATE,BILL_USER," +
						"BILL_NAME,DEPT_CODE,DEPT_NAME,EXEC_DEPT_CODE,EXEC_DEPT_NAME,MR_NO,PAT_NAME,SEX_CODE,BIRTH_DATE,PAT_FAMLIY," +
						"PAT_TEL,DR_NAME,UP_LOAD,LOAD_DOWN,FLG" +
						" FROM" +
					"  (SELECT  A.BILL_FLG,'N' AS HRP_FLG,X.RECEIPT_NO,A.INV_CODE,A.ORDER_CODE,A.ORDER_DESC,REXP_CODE,"
					+" B.CHN_DESC AS REXP_DESC,'001' AS CORP_CODE,-A.AR_AMT AR_AMT,-A.DOSAGE_QTY DOSAGE_QTY,X.BILL_DATE AS ACCOUNT_DATE,"
					+" A.EXEC_DR_CODE BILL_USER,C.USER_NAME AS BILL_NAME,A.DEPT_CODE,D.DEPT_CHN_DESC AS DEPT_NAME,"
					+" A.EXEC_DEPT_CODE,E.COST_CENTER_CHN_DESC AS EXEC_DEPT_NAME,"
					+" A.MR_NO,F.PAT_NAME,G.CHN_DESC AS SEX_CODE,F.BIRTH_DATE,F.CONTACTS_NAME AS PAT_FAMLIY,"
					+" F.TEL_HOME AS PAT_TEL, H.USER_NAME AS DR_NAME,CASE WHEN A.BIL_HIGH_FINANCE_FLG_RETURN='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN,'N' FLG  "
					+" FROM OPD_ORDER_HISTORY A,SYS_DICTIONARY B,SYS_OPERATOR C,SYS_DEPT D,SYS_COST_CENTER E,SYS_PATINFO F,SYS_DICTIONARY G,SYS_OPERATOR H,SYS_FEE I,BIL_OPB_RECP W,BIL_OPB_RECP X"
					+" WHERE "+dateName.replaceAll("A.BILL_DATE", "X.BILL_DATE").replaceFirst("AND", "")
					+" AND A.RECEIPT_NO=W.RECEIPT_NO"
					+" AND A.CASE_NO = W.CASE_NO"
					+" AND A.CASE_NO = X.CASE_NO"
					+" AND W.RESET_RECEIPT_NO = X.RECEIPT_NO"
					+" AND B.GROUP_ID='SYS_CHARGE' AND B.ID=A.REXP_CODE"
					+" AND A.EXEC_DR_CODE=C.USER_ID(+)"//�շ�Ա
					+" AND A.DEPT_CODE=D.DEPT_CODE(+)"//��������
					+" AND A.MR_NO=F.MR_NO"
					+" AND G.GROUP_ID='SYS_SEX' AND G.ID=F.SEX_CODE"
					+" AND A.EXEC_DEPT_CODE=E.COST_CENTER_CODE(+)"
					+" AND A.DR_CODE=H.USER_ID(+)"
					+" AND A.ORDER_CODE=I.ORDER_CODE"
					+" AND I.SUPPLIES_TYPE='1' AND A.INV_CODE IS NOT NULL "
					+
					"  )) " +
					" SELECT B.* FROM B " +
					" UNION ALL " +
					" SELECT C.* FROM C " +
					" UNION ALL " +
					" SELECT D.* FROM D ";
				break;
			case 2:
				dateName=" AND A.BILL_DATE BETWEEN"+
				" TO_DATE('"+parm.getValue("START_DATE")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("END_DATE")+"', 'YYYYMMDDHH24MISS') ";
				sql="SELECT ROW_NUMBER () OVER (ORDER BY A.BILL_NO DESC) AS TID,CASE WHEN A.TOT_AMT <> 0 THEN 'Y' ELSE 'N' END AS BILL_FLG,'N' AS HRP_FLG," 
					+" A.CASE_NO||A.CASE_NO_SEQ||A.SEQ_NO AS RECEIPT_NO,A.INV_CODE,A.ORDER_CODE, Q.ORDER_DESC,"//��š��˷ѱ�ǡ�HRP�����ʶ���վݺš����롢ҽ�����롢ҽ������
					+" A.REXP_CODE,B.CHN_DESC AS REXP_DESC,'001' AS CORP_CODE,A.TOT_AMT AS AR_AMT,A.DOSAGE_QTY,A.BILL_DATE AS ACCOUNT_DATE,"//�վݷ�����롢�վݷ������ơ���˾�������շѽ��շ��������շ�����
					+" A.EXE_DR_CODE AS BILL_USER,F.USER_NAME AS BILL_NAME,A.DEPT_CODE,D.DEPT_CHN_DESC AS DEPT_NAME,"//�շ�Ա���ţ��շ������������ұ��롢��������
					+" A.EXE_DEPT_CODE AS EXEC_DEPT_CODE,E.DEPT_CHN_DESC AS EXEC_DEPT_NAME,"//ִ�п��ұ��������
					+" M.MR_NO,N.PAT_NAME,N.BIRTH_DATE,"//�����š���������������
					+" L.CHN_DESC  AS SEX_CODE,N.CONTACTS_NAME AS PAT_FAMLIY,N.TEL_HOME AS PAT_TEL,"//�Ա𡢼�ͥ��ϵ�ˡ���ϵ�绰
					+" G.USER_NAME AS DR_NAME,"//����ҽ��
					+" I.OPT_CHN_DESC,H.OP_DATE,CASE WHEN A.BIL_HIGH_FINANCE_FLG='Y' THEN '���ϴ�' ELSE 'δ�ϴ�' END UP_LOAD,'δ���' LOAD_DOWN,'N' FLG ,A.CASE_NO,A.CASE_NO_SEQ,A.SEQ_NO  "//�������ơ���������
					+" FROM IBS_ORDD A,SYS_DICTIONARY B,SYS_DEPT D,SYS_DEPT E,SYS_OPERATOR F,SYS_OPERATOR G," 
					+" OPE_OPDETAIL H,SYS_OPERATIONICD I,ADM_INP M,SYS_PATINFO N,SYS_DICTIONARY L,SYS_FEE Q"
					+" WHERE B.GROUP_ID='SYS_CHARGE' AND B.ID=A.REXP_CODE"
					+" AND A.DEPT_CODE=D.DEPT_CODE(+)"
					+" AND A.EXE_DEPT_CODE=E.DEPT_CODE(+)"
					+" AND A.EXE_DR_CODE=F.USER_ID"
					+" AND A.CASE_NO=M.CASE_NO"
					+" AND M.MR_NO=N.MR_NO"
					+" AND L.GROUP_ID='SYS_SEX' AND L.ID=N.SEX_CODE"
					+" AND A.DR_CODE=G.USER_ID"
					+" AND A.CASE_NO=H.CASE_NO(+)  AND H.OP_CODE1=I.OPERATION_ICD(+)"
					+" AND A.ORDER_CODE=Q.ORDER_CODE AND Q.SUPPLIES_TYPE='1'  AND A.INV_CODE IS NOT NULL "+dateName;
				break;
			default:
				break;
			}
			System.out.println("��ֵ�ĲĲ�ѯsql:::"+sql);
			parm=new TParm(TJDODBTool.getInstance().select(sql));
		} catch (Exception e) {
			if(isDebug){
				System.out.println("come in class: BILFinanceTool ��method ��onQureyByBillDate");
				e.printStackTrace();
			}
		}
		return parm;
	}
	/**
	 * ҩƷ����
	 * pangben 2016-6-15
	 * @param parm
	 * @return 
	 */
	public TParm onQueryPhaData(TParm parm){
		String sDate=parm.getValue("START_DATE").toString();
		String eDate=parm.getValue("END_DATE").toString();
		String dateName1=" BETWEEN "+" TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+
		eDate+"', 'YYYYMMDDHH24MISS') ";
		String where="";
		String sql="";
	
		if(null!=parm.getValue("BUSITYPE_NAME")&&parm.getValue("BUSITYPE_NAME").length()>0){
			String sql2 =
	            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2,DESCRIPTION FROM SYS_DICTIONARY " +
	            "WHERE GROUP_ID='BIL_FINANCE_PHA' AND ID='"+parm.getValue("BUSITYPE_NAME")+"'";
			TParm result=new TParm(TJDODBTool.getInstance().select(sql2));
			if(result.getErrCode()<0){
				return result;
			}
			if(result.getCount()<=0){
				result=new TParm();
				result.setErr(-1,"������ҩƷ������");
				return result;
			}
			String []valueName={"TYPE_CODE","ORDER_CODE"};
			for (int i = 0; i < valueName.length; i++) {
				if(null!=parm.getValue(valueName[i]) && parm.getValue(valueName[i]).length()>0){
					where+=" AND A."+valueName[i]+"='"+parm.getValue(valueName[i])+"' ";
				}
			}
			
			//[1,ҩƷ�ɹ����],[2,ҩƷ����],[3,��������]]
			if(result.getValue("NAME",0).trim().equals("ҩƷ�ɹ����")){
				where+=" AND M.ORG_CODE IN("+result.getValue("DESCRIPTION",0)+") ";
				if(null!=parm.getValue("ORG_CODE") && parm.getValue("ORG_CODE").length()>0){
					where+=" AND M.ORG_CODE='"+parm.getValue("ORG_CODE")+"' ";
				}
				if(null!=parm.getValue("SUP_CODE") && parm.getValue("SUP_CODE").length()>0){
					where+=" AND M.SUP_CODE='"+parm.getValue("SUP_CODE")+"' ";
				}
				sql="SELECT '' TID,'' AS DEPT_CODE, '' AS DEPT_DESC, '' AS EXE_DEPT_CODE, "+
		        "'' AS EXE_DEPT_DESC, M.ORG_CODE AS PHA_CODE, S.DEPT_CHN_DESC AS PHA_DESC,'"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ�ɹ����' AS BILL_TYPE,"+
		        "TO_CHAR(M.CHECK_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,"+
		        "D.INVOICE_AMT AS AR_AMT, M.SUP_CODE , J.SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
		        "FROM IND_VERIFYINM M, IND_VERIFYIND D, PHA_BASE A,SYS_DEPT S,SYS_SUPPLIER J,SYS_OPERATOR H,SYS_DICTIONARY P "+
		        "WHERE M.VERIFYIN_NO = D.VERIFYIN_NO AND D.ORDER_CODE = A.ORDER_CODE AND M.ORG_CODE=S.DEPT_CODE AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' "+
		        "AND M.SUP_CODE=J.SUP_CODE  AND D.UPDATE_FLG IN ('1', '3') AND M.CHECK_USER=H.USER_ID AND M.CHECK_DATE "+dateName1+where+
		        "UNION ALL "+
		        //�˻�
		        "SELECT '' TID,'' AS DEPT_CODE, '' AS DEPT_DESC, '' AS EXE_DEPT_CODE,"+
		        "'' AS EXE_DEPT_DESC, M.ORG_CODE AS PHA_CODE, S.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ�ɹ����' AS BILL_TYPE, "+
		        "TO_CHAR(M.CHECK_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC, "+
		        "-D.AMT AS AR_AMT, M.SUP_CODE , J.SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
		        "FROM IND_REGRESSGOODSM M, IND_REGRESSGOODSD D, PHA_BASE A,SYS_DEPT S,SYS_SUPPLIER J,SYS_OPERATOR H,SYS_DICTIONARY P "+
		        "WHERE M.REGRESSGOODS_NO = D.REGRESSGOODS_NO AND D.ORDER_CODE = A.ORDER_CODE AND M.ORG_CODE=S.DEPT_CODE AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' "+
		        "AND M.SUP_CODE=J.SUP_CODE AND M.CHECK_USER=H.USER_ID  AND D.UPDATE_FLG IN ('1', '3') AND M.CHECK_USER IS NOT NULL "+
		        "AND M.CHECK_DATE "+dateName1+where;
				System.out.println("ҩƷ�������ҩƷ�ɹ����sql:::"+sql);
			}else if(result.getValue("NAME",0).trim().equals("ҩƷ����")){
				 where+=" AND M.EXEC_DEPT_CODE IN("+result.getValue("DESCRIPTION",0)+") ";
				 String where1="";
				 if(null!=parm.getValue("ORG_CODE") && parm.getValue("ORG_CODE").length()>0){
					where+=" AND M.EXEC_DEPT_CODE='"+parm.getValue("ORG_CODE")+"' ";
					where1+=" AND M.ORG_CODE='"+parm.getValue("ORG_CODE")+"' ";
				 }
				 if(null!=parm.getValue("DEPT_CODE") && parm.getValue("DEPT_CODE").length()>0){
						where+=" AND M.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
						where1+=" AND M.ORG_CODE='"+parm.getValue("DEPT_CODE")+"' ";
				 }
				 // String dateName2=sDate.substring(0,8);
				 // String dateName3=eDate.substring(0,8);
				 //����ҩ����ҩ
				sql="SELECT  '' TID,M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
		        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC,'"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ����' AS BILL_TYPE, "+
		        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE,A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC, "+
		        "M.VERIFYIN_PRICE1*M.DISPENSE_QTY1 + M.VERIFYIN_PRICE2*M.DISPENSE_QTY2 +M.VERIFYIN_PRICE3*M.DISPENSE_QTY3 AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'����' VDEF1 "+
		        "FROM OPD_ORDER M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H,SYS_DICTIONARY P "+
		        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_DOSAGE_CODE=H.USER_ID  AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
		        "AND  M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE AND M.BILL_FLG='Y' "+
		        "AND M.PHA_DOSAGE_DATE "+dateName1+where+
		        "AND M.PHA_RETN_DATE IS NULL "+
		        "UNION ALL "+
		        //סԺ��ҩ
		        "SELECT '' TID, M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
		        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ����' AS BILL_TYPE, "+
		        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,"+
		        "M.VERIFYIN_PRICE1*M.DISPENSE_QTY1 + M.VERIFYIN_PRICE2*M.DISPENSE_QTY2 +M.VERIFYIN_PRICE3*M.DISPENSE_QTY3 AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'סԺ' VDEF1 "+
		        "FROM ODI_DSPNM M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H,SYS_DICTIONARY P "+
		        "WHERE M.ORDER_CODE = A.ORDER_CODE  AND M.PHA_DOSAGE_CODE=H.USER_ID AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
		        "AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
		        "AND M.PHA_DOSAGE_DATE "+dateName1+where+
		        "AND M.PHA_RETN_DATE IS NULL "+
		        "UNION ALL "+
		        //������ҩ
		        "SELECT  '' TID,M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
		        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC,'"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ����' AS BILL_TYPE, "+
		        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE,A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC, "+
		        "-(M.VERIFYIN_PRICE1*M.DISPENSE_QTY1 + M.VERIFYIN_PRICE2*M.DISPENSE_QTY2 +M.VERIFYIN_PRICE3*M.DISPENSE_QTY3) AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'����' VDEF1 "+
		        "FROM OPD_ORDER_HISTORY M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H,SYS_DICTIONARY P "+
		        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_DOSAGE_CODE=H.USER_ID  AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
		        "AND  M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE AND M.BILL_FLG='Y' "+
		        "AND M.PHA_RETN_DATE "+dateName1+where+
		        "UNION ALL "+
				 //סԺ��ҩ
		        "SELECT '' TID, M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
		        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ����' AS BILL_TYPE, "+
		        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,"+
		        "-(M.VERIFYIN_PRICE1*M.DISPENSE_QTY1 + M.VERIFYIN_PRICE2*M.DISPENSE_QTY2 +M.VERIFYIN_PRICE3*M.DISPENSE_QTY3) AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'סԺ' VDEF1 "+
		        "FROM ODI_DSPNM M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H,SYS_DICTIONARY P "+
		        "WHERE M.ORDER_CODE = A.ORDER_CODE  AND M.PHA_DOSAGE_CODE=H.USER_ID AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
		        "AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
		        "AND M.PHA_RETN_DATE "+dateName1+where;
				System.out.println("ҩƷ�������ҩƷ����sql:::"+sql);
			}else {
				  //String where1="";
				  //where+=" AND M.ORG_CODE IN("+result.getValue("DESCRIPTION",0)+") ";	
				  where+=" AND M.TO_ORG_CODE IN("+result.getValue("DESCRIPTION",0)+") ";	
				  if(null!=parm.getValue("ORG_CODE") && parm.getValue("ORG_CODE").length()>0){
						//where+=" AND M.ORG_CODE='"+parm.getValue("ORG_CODE")+"' ";
						where+=" AND M.TO_ORG_CODE ='"+parm.getValue("ORG_CODE")+"' ";
				  }
//				  String dateName2=sDate.substring(0,8);
//				  sql=" SELECT '' TID,M.APP_ORG_CODE AS DEPT_CODE,S.DEPT_CHN_DESC AS DEPT_DESC,M.APP_ORG_CODE AS EXE_DEPT_CODE,"+
//			       "S.DEPT_CHN_DESC AS EXE_DEPT_DESC,M.TO_ORG_CODE AS PHA_CODE,X.DEPT_CHN_DESC AS PHA_DESC,"+
//			       "'"+result.getValue("ID",0)+"' AS BILL_NAME,'��������' AS BILL_TYPE,TO_CHAR (M.DISPENSE_DATE, 'yyyy-MM-dd') AS ACCOUNT_DATE,"+
//			       "A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC, CASE WHEN D.UNIT_CODE=K.STOCK_UNIT  "+
//			       " THEN D.INVENT_PRICE*D.ACTUAL_QTY  WHEN D.UNIT_CODE=K.DOSAGE_UNIT THEN D.VERIFYIN_PRICE*D.ACTUAL_QTY "+
//			       " ELSE D.VERIFYIN_PRICE*D.ACTUAL_QTY  END  AR_AMT ,D.SUP_CODE,J.SUP_CHN_DESC,'��' INOUT,'δ�ϴ�' AS UP_LOAD,"+
//			       "'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
//			       " FROM IND_DISPENSEM M, IND_DISPENSED D,PHA_BASE A,SYS_DEPT S,SYS_DEPT X,SYS_SUPPLIER J,SYS_OPERATOR H,"+
//			       "SYS_DICTIONARY P,PHA_TRANSUNIT K "+
//			       "WHERE  M.DISPENSE_NO = D.DISPENSE_NO AND D.SUP_CODE = J.SUP_CODE AND D.ORDER_CODE = A.ORDER_CODE "+
//			       "AND M.DISPENSE_USER = H.USER_ID AND A.TYPE_CODE = P.ID AND P.GROUP_ID = 'SYS_PHATYPE' "+
//			       "AND M.APP_ORG_CODE = S.DEPT_CODE AND M.TO_ORG_CODE = X.DEPT_CODE  AND D.ORDER_CODE = K.ORDER_CODE "+
//			       "AND M.WAREHOUSING_DATE "+dateName1+where1+
//			       "AND M.REQTYPE_CODE = 'DEP' "+
//			       "UNION ALL "+
//			       "SELECT '' TID,M.APP_ORG_CODE AS DEPT_CODE,S.DEPT_CHN_DESC AS DEPT_DESC,M.APP_ORG_CODE AS EXE_DEPT_CODE,"+
//			       "S.DEPT_CHN_DESC AS EXE_DEPT_DESC,M.TO_ORG_CODE AS PHA_CODE,X.DEPT_CHN_DESC AS PHA_DESC,"+
//			       "'"+result.getValue("ID",0)+"' AS BILL_NAME,'��������' AS BILL_TYPE, TO_CHAR (M.DISPENSE_DATE, 'yyyy-MM-dd') AS ACCOUNT_DATE,"+
//			       "A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,CASE WHEN D.UNIT_CODE=K.STOCK_UNIT  "+
//			       "THEN D.INVENT_PRICE*D.ACTUAL_QTY  WHEN D.UNIT_CODE=K.DOSAGE_UNIT THEN D.VERIFYIN_PRICE*D.ACTUAL_QTY "+ 
//			       " ELSE D.VERIFYIN_PRICE*D.ACTUAL_QTY  END  AR_AMT ,D.SUP_CODE,J.SUP_CHN_DESC,'��' INOUT,'δ�ϴ�' AS UP_LOAD,"+
//			       "'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
//			       "FROM IND_DISPENSEM M,IND_DISPENSED D,PHA_BASE A,SYS_DEPT S,SYS_DEPT X,SYS_SUPPLIER J,"+
//			       "SYS_OPERATOR H, SYS_DICTIONARY P,PHA_TRANSUNIT K "+
//			       "WHERE     M.DISPENSE_NO = D.DISPENSE_NO AND D.SUP_CODE = J.SUP_CODE AND D.ORDER_CODE = A.ORDER_CODE "+
//			       "AND M.DISPENSE_USER = H.USER_ID AND A.TYPE_CODE = P.ID AND P.GROUP_ID = 'SYS_PHATYPE' "+
//			       "AND M.APP_ORG_CODE = S.DEPT_CODE AND M.TO_ORG_CODE = X.DEPT_CODE AND D.ORDER_CODE = K.ORDER_CODE "+
//			       "AND M.WAREHOUSING_DATE "+dateName1+where1+
//			       "AND M.REQTYPE_CODE = 'GIF' "+

				  
				  
				  sql= "SELECT '' TID,M.APP_ORG_CODE AS DEPT_CODE,  S.DEPT_CHN_DESC AS DEPT_DESC, M.APP_ORG_CODE AS EXE_DEPT_CODE,"+
			        "S.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.TO_ORG_CODE AS PHA_CODE, X.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, '��������' AS BILL_TYPE,"+
			        "TO_CHAR(M.DISPENSE_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,D.ACTUAL_QTY AS DOSAGE,"+
			        " CASE WHEN D.UNIT_CODE=K.STOCK_UNIT  "+
			       " THEN D.INVENT_PRICE*D.ACTUAL_QTY  WHEN D.UNIT_CODE=K.DOSAGE_UNIT THEN D.VERIFYIN_PRICE*D.ACTUAL_QTY "+
			       " ELSE D.VERIFYIN_PRICE*D.ACTUAL_QTY  END  AR_AMT, D.SUP_CODE ,J.SUP_CHN_DESC  , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
			        "FROM IND_DISPENSEM M, IND_DISPENSED D, PHA_BASE A,SYS_DEPT S,SYS_DEPT X,SYS_SUPPLIER J,SYS_OPERATOR H,SYS_DICTIONARY P,PHA_TRANSUNIT K "+
			        "WHERE M.DISPENSE_NO = D.DISPENSE_NO  AND D.SUP_CODE=J.SUP_CODE AND D.ORDER_CODE=K.ORDER_CODE "+
			        "AND D.ORDER_CODE = A.ORDER_CODE AND M.DISPENSE_USER =H.USER_ID AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
			        "AND M.APP_ORG_CODE=S.DEPT_CODE AND M.TO_ORG_CODE =X.DEPT_CODE "+
			        "AND M.DISPENSE_DATE "+dateName1+where+
			        "AND M.REQTYPE_CODE IN ('EXM','COS') "+
			        "UNION ALL "+
			        //���ұ�ҩ������
			        "SELECT '' TID,M.APP_ORG_CODE AS DEPT_CODE,  S.DEPT_CHN_DESC AS DEPT_DESC, M.APP_ORG_CODE AS EXE_DEPT_CODE, "+
			        "S.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.TO_ORG_CODE AS PHA_CODE, X.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, '��������' AS BILL_TYPE, "+
			        "TO_CHAR(M.DISPENSE_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,"+
			        "D.ACTUAL_QTY*K.DOSAGE_QTY DOSAGE, - D.ACTUAL_QTY*K.DOSAGE_QTY * D.VERIFYIN_PRICE AS AR_AMT, D.SUP_CODE ,J.SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
			        "FROM IND_DISPENSEM M, IND_DISPENSED D, PHA_BASE A,SYS_DEPT S,SYS_DEPT X,SYS_SUPPLIER J,SYS_OPERATOR H,SYS_DICTIONARY P,PHA_TRANSUNIT K "+
			        "WHERE M.DISPENSE_NO = D.DISPENSE_NO AND D.SUP_CODE=J.SUP_CODE AND D.ORDER_CODE=K.ORDER_CODE "+
			        "AND D.ORDER_CODE = A.ORDER_CODE AND M.DISPENSE_USER =H.USER_ID AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
			        "AND M.APP_ORG_CODE=S.DEPT_CODE AND M.TO_ORG_CODE =X.DEPT_CODE "+
			        "AND M.DISPENSE_DATE "+dateName1+where+
			        "AND M.REQTYPE_CODE = 'DRT' AND M.UPDATE_FLG ='3' ";
//			       "UNION ALL "+
//			       "SELECT  '' TID,M.ORG_CODE AS DEPT_CODE, D.DEPT_CHN_DESC AS DEPT_DESC, M.ORG_CODE AS EXE_DEPT_CODE, "+
//			        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.ORG_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, '��������' AS BILL_TYPE, "+
//			        "M.TRANDATE AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC, "+
//			        "SUM(M.REGRESSDRUG_QTY*M.VERIFYIN_PRICE)  AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'' VDEF1 "+
//			        "FROM IND_DDSTOCK M,SYS_DEPT D,PHA_BASE A, SYS_DICTIONARY P,SYS_OPERATOR H " +
//			        "WHERE M.ORG_CODE=D.DEPT_CODE AND M.ORDER_CODE=A.ORDER_CODE  AND A.TYPE_CODE = P.ID AND P.GROUP_ID = 'SYS_PHATYPE' AND D.OPT_USER=H.USER_ID " +
//			        "AND M.TRANDATE = '"+dateName2+"'"+where+" AND M.REGRESSDRUG_QTY !=0 GROUP BY M.ORG_CODE,D.DEPT_CHN_DESC,M.TRANDATE,A.TYPE_CODE,P.CHN_DESC,H.USER_NAME";
			        //������ҩ
//			        "UNION ALL "+
//			        "SELECT  '' TID,M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
//			        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ����' AS BILL_TYPE, "+
//			        "TO_CHAR(M.PHA_RETN_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC, "+
//			        "M.AR_AMT AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'����' VDEF1 "+
//			        "FROM OPD_ORDER M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H,SYS_DICTIONARY P "+
//			        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_RETN_CODE=H.USER_ID AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
//			        "AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
//			        "AND M.PHA_RETN_DATE "+dateName1+where+
//			        "AND M.PHA_RETN_CODE IS NOT NULL "+
//			        "UNION ALL "+
//			        //סԺ��ҩ
//			        "SELECT  '' TID,M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE,"+
//			        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, '"+result.getValue("ID",0)+"' AS BILL_NAME, 'ҩƷ����' AS BILL_TYPE,"+
//			        "TO_CHAR(M.PHA_RETN_DATE,'yyyy-MM-dd') AS ACCOUNT_DATE, A.TYPE_CODE PHA_TYPE,P.CHN_DESC AS PHA_TYPE_DESC,"+
//			        "M.DC_TOT AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'δ�ϴ�' AS UP_LOAD,'δ���' AS LOAD_DOWN,H.USER_NAME,'סԺ' VDEF1 "+
//			        "FROM ODI_DSPNM M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H,SYS_DICTIONARY P "+
//			        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_RETN_CODE=H.USER_ID AND A.TYPE_CODE=P.ID AND P.GROUP_ID='SYS_PHATYPE' " +
//			        "AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
//			        "AND M.PHA_RETN_DATE "+dateName1+where+
//			        "AND M.PHA_RETN_CODE IS NOT NULL ";
				  System.out.println("ҩƷ������ݿ�������sql:::"+sql);
			}
		}
		
//		String sql="SELECT '' AS DEPT_CODE, '' AS DEPT_DESC, '' AS EXE_DEPT_CODE, "+
//        "'' AS EXE_DEPT_DESC, M.ORG_CODE AS PHA_CODE, S.DEPT_CHN_DESC AS PHA_DESC, 'ҩƷ�ɹ����' AS BILL_TYPE,"+
//        "TO_CHAR(M.CHECK_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C' "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C' "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC,"+
//        "D.INVOICE_AMT AS AR_AMT, M.SUP_CODE , J.SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM IND_VERIFYINM M, IND_VERIFYIND D, PHA_BASE A,SYS_DEPT S,SYS_SUPPLIER J,SYS_OPERATOR H "+
//        "WHERE M.VERIFYIN_NO = D.VERIFYIN_NO AND D.ORDER_CODE = A.ORDER_CODE AND M.ORG_CODE=S.DEPT_CODE  "+
//        "AND M.SUP_CODE=J.SUP_CODE AND M.CHECK_USER=H.USER_ID AND M.CHECK_DATE "+dateName1+where+
//        "UNION ALL "+
//        //�˻�
//        "SELECT '' AS DEPT_CODE, '' AS DEPT_DESC, '' AS EXE_DEPT_CODE,"+
//        "'' AS EXE_DEPT_DESC, M.ORG_CODE AS PHA_CODE, S.DEPT_CHN_DESC AS PHA_DESC, 'ҩƷ�ɹ����' AS BILL_TYPE, "+
//        "TO_CHAR(M.CHECK_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C'  "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C' "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC, "+
//        "-D.AMT AS AR_AMT, M.SUP_CODE , J.SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD ,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM IND_REGRESSGOODSM M, IND_REGRESSGOODSD D, PHA_BASE A,SYS_DEPT S,SYS_SUPPLIER J,SYS_OPERATOR H "+
//        "WHERE M.REGRESSGOODS_NO = D.REGRESSGOODS_NO AND D.ORDER_CODE = A.ORDER_CODE AND M.ORG_CODE=S.DEPT_CODE "+
//        "AND M.SUP_CODE=J.SUP_CODE AND M.CHECK_USER=H.USER_ID "+
//        "AND M.CHECK_DATE "+dateName1+where+
//        "UNION ALL "+
//        //����ҩ����ҩ
//        "SELECT  M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
//        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, 'ҩƷ����' AS BILL_TYPE, "+
//        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C'  "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C' "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC, "+
//        "M.VERIFYIN_PRICE1 AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD ,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM OPD_ORDER M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H "+
//        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_DOSAGE_CODE=H.USER_ID  AND  M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE AND M.BILL_FLG='Y' "+
//        "AND M.PHA_DOSAGE_DATE "+dateName1+where1+
//        "AND M.PHA_RETN_DATE IS NULL "+
//        "UNION ALL "+
//        //������ҩ
//        "SELECT  M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
//        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, 'ҩƷ����' AS BILL_TYPE, "+
//        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C'  "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C'  "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC, "+
//        "-M.VERIFYIN_PRICE1 AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM OPD_ORDER M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H "+
//        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_RETN_CODE=H.USER_ID AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
//        "AND M.PHA_RETN_DATE "+dateName1+where1+
//        "AND M.PHA_RETN_CODE IS NOT NULL "+
//        "UNION ALL "+
//        //סԺ��ҩ
//        "SELECT  M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE, "+
//        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, 'ҩƷ����' AS BILL_TYPE, "+
//        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C'  "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C' "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC,"+
//        "M.VERIFYIN_PRICE1 AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM ODI_DSPNM M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H "+
//        "WHERE M.ORDER_CODE = A.ORDER_CODE  AND M.PHA_DOSAGE_CODE=H.USER_ID AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
//        "AND M.PHA_DOSAGE_DATE "+dateName1+where1+
//        "AND M.PHA_RETN_DATE IS NULL "+
//        "UNION ALL "+
//        //סԺ��ҩ
//        "SELECT  M.DEPT_CODE AS DEPT_CODE, S.DEPT_CHN_DESC AS DEPT_DESC, M.EXEC_DEPT_CODE AS EXE_DEPT_CODE,"+
//        "D.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.EXEC_DEPT_CODE AS PHA_CODE, D.DEPT_CHN_DESC AS PHA_DESC, 'ҩƷ����' AS BILL_TYPE,"+
//        "TO_CHAR(M.PHA_DOSAGE_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C'  "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C' "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC,"+
//        "-M.VERIFYIN_PRICE1 AS AR_AMT, ''SUP_CODE , ''SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM ODI_DSPNM M, PHA_BASE A,SYS_DEPT S,SYS_DEPT D,SYS_OPERATOR H "+
//        "WHERE M.ORDER_CODE = A.ORDER_CODE AND M.PHA_RETN_CODE=H.USER_ID AND M.DEPT_CODE=S.DEPT_CODE AND M.EXEC_DEPT_CODE=D.DEPT_CODE "+
//        "AND M.PHA_RETN_DATE "+dateName1+where1+
//        "AND M.PHA_RETN_CODE IS NOT NULL "+
//        "UNION ALL "+
//        //���ұ�ҩ������
//        "SELECT M.APP_ORG_CODE AS DEPT_CODE,  S.DEPT_CHN_DESC AS DEPT_DESC, M.APP_ORG_CODE AS EXE_DEPT_CODE,"+
//        "S.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.TO_ORG_CODE AS PHA_CODE, X.DEPT_CHN_DESC AS PHA_DESC, '��������' AS BILL_TYPE,"+
//        "TO_CHAR(M.DISPENSE_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C' "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C' "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC,"+
//        "D.VERIFYIN_PRICE AS AR_AMT, D.SUP_CODE ,J.SUP_CHN_DESC  , '��' INOUT,'' AS UP_LOAD,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM IND_DISPENSEM M, IND_DISPENSED D, PHA_BASE A,SYS_DEPT S,SYS_DEPT X,SYS_SUPPLIER J,SYS_OPERATOR H "+
//        "WHERE M.DISPENSE_NO = D.DISPENSE_NO  AND D.SUP_CODE=J.SUP_CODE "+
//        "AND D.ORDER_CODE = A.ORDER_CODE AND M.DISPENSE_USER =H.USER_ID AND M.APP_ORG_CODE=S.DEPT_CODE AND M.TO_ORG_CODE =X.DEPT_CODE "+
//        "AND M.DISPENSE_DATE "+dateName1+where2+
//        "AND M.REQTYPE_CODE = 'EXM' "+
//        "UNION ALL "+
//        //���ұ�ҩ������
//        "SELECT M.APP_ORG_CODE AS DEPT_CODE,  S.DEPT_CHN_DESC AS DEPT_DESC, M.APP_ORG_CODE AS EXE_DEPT_CODE, "+
//        "S.DEPT_CHN_DESC AS EXE_DEPT_DESC, M.TO_ORG_CODE AS PHA_CODE, X.DEPT_CHN_DESC AS PHA_DESC, '��������' AS BILL_TYPE, "+
//        "TO_CHAR(M.DISPENSE_DATE,'yyyy/MM/dd') AS BILL_DATE, CASE WHEN A.PHA_TYPE='W' THEN '1' WHEN A.PHA_TYPE='C'  "+
//        "THEN '2' END  AS PHA_TYPE,  CASE WHEN A.PHA_TYPE='W' THEN '��ҩ' WHEN A.PHA_TYPE='C'  "+
//        "THEN '�г�ҩ' END  AS PHA_TYPE_DESC,"+
//        "-D.VERIFYIN_PRICE AS AR_AMT, D.SUP_CODE ,J.SUP_CHN_DESC , '��' INOUT,'' AS UP_LOAD,'' AS LOAD_DOWN,H.USER_NAME "+
//        "FROM IND_DISPENSEM M, IND_DISPENSED D, PHA_BASE A,SYS_DEPT S,SYS_DEPT X,SYS_SUPPLIER J,SYS_OPERATOR H "+
//        "WHERE M.DISPENSE_NO = D.DISPENSE_NO AND D.SUP_CODE=J.SUP_CODE "+
//        "AND D.ORDER_CODE = A.ORDER_CODE AND M.DISPENSE_USER =H.USER_ID AND M.APP_ORG_CODE=S.DEPT_CODE AND M.TO_ORG_CODE =X.DEPT_CODE "+
//        "AND M.DISPENSE_DATE "+dateName1+where2+
//        "AND M.REQTYPE_CODE = 'DRT' ";
		//System.out.println("ҩƷ�������sql:::"+sql);
		TParm result=new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	/**
	 * �ɱ�����add by huangjw 
	 * @param parm
	 * @return
	 */
	public TParm onQueryCostAcount(TParm parm){
		String sDate=parm.getValue("START_DATE").toString();
		String eDate=parm.getValue("END_DATE").toString();
		int type = parm.getInt("TYPE");
		String sql="";
		String dateName="";
		String dateName1="";
		TParm checkParm = null;
		switch(type){
		case 1://����
			dateName=" A.ADM_DATE BETWEEN "+
			" TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS') " ;
			sql="SELECT ROW_NUMBER() OVER(ORDER BY A.REALDEPT_CODE, B.DEPT_CHN_DESC) TID,'����' TYPE,'01' PK_ITEM,'�ż����˴�' ITEMNAME," +
					" A.REALDEPT_CODE PK_DIAGNOSISDEPT,B.DEPT_CHN_DESC DIAGNOSISDEPTNAME," +
					" COUNT (A.ADM_DATE) VOLUME," +
					" '�˴�' MEASURENAME,'δ�ϴ�' UP_LOAD,'δ���' LOAD_DOWN " +
					" FROM REG_PATADM A,SYS_DEPT B WHERE A.REALDEPT_CODE=B.DEPT_CODE AND "+dateName+" " +
					" AND A.SEEN_DR_TIME IS NOT NULL "+
					//" AND ( CASE (SELECT COUNT(MR_NO) CT FROM OPD_ORDER C WHERE C.CASE_NO = A.CASE_NO AND C.RX_NO = 'CLINIC_FEE') WHEN 0 THEN 0 ELSE 1 END  ) = 1 " +
					" GROUP BY A.REALDEPT_CODE, B.DEPT_CHN_DESC ";
			break;
		case 2://סԺ
			dateName=" A.IN_DATE BETWEEN "+
			" TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS') " ;
			dateName1=" A.DS_DATE BETWEEN "+
			" TO_DATE('"+sDate+"','YYYYMMDDHH24MISS') AND TO_DATE('"+eDate+"', 'YYYYMMDDHH24MISS') " ;
			String batchName = sDate.substring(0,8);
			checkParm = new TParm(TJDODBTool.getInstance().select("SELECT * FROM DI_SERVICEVOLEXP_LOG where BATCHNAME = '"+batchName+"'"));
			if(checkParm.getCount() > 0){
				sql=
					" SELECT ROW_NUMBER () OVER (ORDER BY PK_DIAGNOSISDEPT,DIAGNOSISDEPTNAME) AS TID,t.*" +
					" FROM (SELECT 'סԺ' TYPE,PK_ITEM,ITEMNAME,"+
					" PK_DIAGNOSISDEPT,DIAGNOSISDEPTNAME,"+
					" VOLUME,'����' MEASURENAME,"+
					" '���ϴ�' UP_LOAD,'δ���' LOAD_DOWN"+
					" FROM DI_SERVICEVOLEXP_LOG WHERE BATCHNAME = '"+batchName+"'" +
					//" GROUP BY PK_DIAGNOSISDEPT, DIAGNOSISDEPTNAME "+
					" UNION" +
					" SELECT 'סԺ' TYPE," +
					" '03' PK_ITEM," +
					" '��Ժ�˴�' ITEMNAME,A.DS_DEPT_CODE PK_DIAGNOSISDEPT," +
					" B.DEPT_CHN_DESC DIAGNOSISDEPTNAME," +
					" COUNT (DS_DATE) VOLUME," +
					" '��Ժ�˴�' MEASURENAME,'δ�ϴ�' UP_LOAD,'δ���' LOAD_DOWN" +
					" FROM ADM_INP A, SYS_DEPT B" +
					" WHERE A.DS_DEPT_CODE = B.DEPT_CODE AND "+dateName1+"" +
					" GROUP BY A.DS_DEPT_CODE, B.DEPT_CHN_DESC) t";
			}else{
				sql=
				/*"SELECT ROW_NUMBER () OVER (ORDER BY PK_DIAGNOSISDEPT,DIAGNOSISDEPTNAME) AS TID,t.*" +
				" FROM (  SELECT 'סԺ' TYPE,'02' PK_ITEM,'ʵ��ռ�ô�����' ITEMNAME,A.IN_DEPT_CODE PK_DIAGNOSISDEPT," +
				" B.DEPT_CHN_DESC DIAGNOSISDEPTNAME,COUNT (IN_DATE) VOLUME,'����' MEASURENAME,'δ�ϴ�' UP_LOAD,'δ���' LOAD_DOWN" +
				" FROM ADM_INP A, SYS_DEPT B WHERE A.IN_DEPT_CODE = B.DEPT_CODE AND CANCEL_FLG <> 'Y' AND "+dateName+" GROUP BY A.IN_DEPT_CODE,B.DEPT_CHN_DESC" +*/
				" SELECT ROW_NUMBER () OVER (ORDER BY PK_DIAGNOSISDEPT,DIAGNOSISDEPTNAME) AS TID,t.*" +
				" FROM (SELECT 'סԺ' TYPE,'02' PK_ITEM,'ʵ��ռ�ô�����' ITEMNAME,"+
				" B.IN_DEPT_CODE PK_DIAGNOSISDEPT,C.DEPT_CHN_DESC DIAGNOSISDEPTNAME,"+
				" COUNT (A.BED_NO) VOLUME,'����' MEASURENAME,"+
				" 'δ�ϴ�' UP_LOAD,'δ���' LOAD_DOWN"+
				" FROM SYS_BED A,ADM_INP B, SYS_DEPT C "+
				" WHERE A.CASE_NO = B.CASE_NO  "+
				" AND  B.IN_DEPT_CODE = C.DEPT_CODE"+
				" GROUP BY B.IN_DEPT_CODE, C.DEPT_CHN_DESC"+
				" UNION" +
				" SELECT 'סԺ' TYPE," +
				" '03' PK_ITEM," +
				" '��Ժ�˴�' ITEMNAME,A.DS_DEPT_CODE PK_DIAGNOSISDEPT," +
				" B.DEPT_CHN_DESC DIAGNOSISDEPTNAME," +
				" COUNT (DS_DATE) VOLUME," +
				" '��Ժ�˴�' MEASURENAME,'δ�ϴ�' UP_LOAD,'δ���' LOAD_DOWN" +
				" FROM ADM_INP A, SYS_DEPT B" +
				" WHERE A.DS_DEPT_CODE = B.DEPT_CODE AND "+dateName1+"" +
				" GROUP BY A.DS_DEPT_CODE, B.DEPT_CHN_DESC) t";
			}
		}
		System.out.println("�ɱ�����sql111111:::"+sql);
		parm=new TParm(TJDODBTool.getInstance().select(sql));
		if(checkParm != null && checkParm.getCount() > 0){
			parm.setData("LOCAL_LOG_FLG", true);
		}else{
			parm.setData("LOCAL_LOG_FLG", false);
		}
		return parm;
	}
	/**
	 * ɾ��Ӧ������
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiIncomeexpByBatchname(TParm parm,TConnection connection){
		TParm result =update("deleteDiIncomeexpByBatchname",parm,connection);
    	return result;
	}
	/**
	 * ɾ���ɱ�����
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiIncomeCostAcountByBatchname(TParm parm,TConnection connection){
		TParm result =update("deleteCostAcountByBatchname",parm,connection);
		return result;
	}
	/**
	 * ɾ����ֵ�Ĳı�add by huangjw 20141114
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiIncomeHighPriceByBatchname(TParm parm,TConnection connection){
		TParm result =update("deleteDiIncomeHighPriceByBatchname",parm,connection);
		return result;
	}
	/**
	 * ɾ��ҩƷ���� 
	 * pangben 2016-6-16
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDidrugsByBatchname(TParm parm,TConnection connection){
		TParm result =update("deleteDidrugsByBatchname",parm,connection);
		return result;
	}
	/**
	 * �ɱ�log��ɾ��
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiExpLog(TParm parm,TConnection connection){
		TParm result =update("deleteDiExpLog",parm,connection);
    	return result;
	}
	/**
	 * �ɱ�log�����
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertDiExpLog(TParm parm,TConnection connection){
		TParm result =update("insertDiExpLog",parm,connection);
    	return result;
	}
	/**
	 * ҩƷ������
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertDidrugs(TParm parm,TConnection connection){
		TParm result =update("insertDidrugs",parm,connection);
    	return result;
	}
	/**
	 * ������̯����
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm insertApportion(TParm parm,TConnection connection){
		TParm result =update("insertApportion",parm,connection);
    	return result;
	}
	
	/**
	 * ɾ��ʵ������
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiIncomerealByBatchname(TParm parm,TConnection connection){
		TParm result =update("deleteDiIncomerealByBatchname",parm,connection);
    	return result;
	}
	/**
	 * ɾ��Ԥ��������
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiIncomepreByBatchname(TParm parm,TConnection connection){
		TParm result =update("deleteDiIncomepreByBatchname",parm,connection);
    	return result;
	}
	/**
	 * ɾ��Ԥ��������(����)
	 * @param parm
	 * @param connection
	 * @return
	 */
	public TParm deleteDiIncomepreByBatchnameO(TParm parm,TConnection connection){
		TParm result =update("deleteDiIncomepreByBatchnameO",parm,connection);
    	return result;
	}
}

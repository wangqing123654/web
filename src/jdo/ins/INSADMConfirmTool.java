package jdo.ins;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Vector;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>
 * Title: �ʸ�ȷ��������ȷ��
 * </p>
 * 
 * <p>
 * Description: �ʸ�ȷ��������ȷ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author pangben 20111128
 * @version 1.0
 */
public class INSADMConfirmTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	public static INSADMConfirmTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return INSADMConfirmTool
	 */
	public static INSADMConfirmTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSADMConfirmTool();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public INSADMConfirmTool() {
		setModuleName("ins\\INSADMConfirmModule.x");
		onInit();
	}

	/**
	 * ��ѯ�Ƿ�����ʸ�ȷ����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryADMConfirm(TParm parm) {
		TParm result = query("queryADMConfirm", parm);
		return result;
	}

	/**
	 * �������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm insertConfirmApply(TParm parm, TConnection conn) {
		TParm result = update("insertConfirmApply", parm, conn);
		return result;
	}

	/**
	 * �޸�����
	 * 
	 * @param parm
	 * @return
	 */
	public TParm updateConfirmApply(TParm parm,TConnection conn) {
		TParm result = update("updateConfirmApply", parm,conn);
		return result;
	}
	/**
	 * סԺ���÷ָ��ѯ����������Ϣ
	 * @param parm
	 * @return
	 */
	public TParm INS_Adm_Seq(TParm parm) {
		
		DateFormat df = new SimpleDateFormat("yyyy");
		//�����ֲ������SQL���� TYPE=SINGLE
		String singleSql="";
		if (null!=parm.getValue("TYPE")&& parm.getValue("TYPE").equals("SINGLE")) 
			singleSql=" AND  C.SDISEASE_CODE IS NOT NULL";
		else
			singleSql=" AND  C.SDISEASE_CODE IS NULL";
		
		if (null!=parm.getValue("CASE_NO") && parm.getValue("CASE_NO").toString().length()>0){
			singleSql+=" AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
		}
		if (null!=parm.getValue("MR_NO") && parm.getValue("MR_NO").toString().length()>0){
			singleSql+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
		}
		String date = df.format(SystemTool.getInstance().getDate())
				+ "/01/01 00:00:00";// ҽ������
		String SQL = " SELECT CASE SUBSTR(C.CONFIRM_NO,1,2) WHEN 'KN' THEN '"
				+ date
				+ "' ELSE TO_CHAR(A.IN_DATE,'YYYY/MM/DD') END AS IN_DATE, "
				+ // ҽ������
				" A.CASE_NO,C.CONFIRM_NO,C.PAT_NAME,C.SEX_CODE,A.CTZ1_CODE,C.IDNO,A.IPD_NO, "
				+ " CASE IN_STATUS WHEN '0' THEN '�ʸ�ȷ����¼��' WHEN '1' THEN '�����ѽ���' WHEN '2' THEN '�������ϴ�' "
				+ " WHEN '3' THEN '���������' WHEN '4' THEN '������֧��' WHEN '5' THEN '����ȷ����' WHEN '6' THEN '�����ʸ�ȷ����ʧ��' "
				+ " WHEN '7' THEN '�ʸ�ȷ���������' ELSE '' END  AS IN_STATUS,A.MR_NO,C.PAT_AGE "
				+ " FROM ADM_INP A,SYS_CTZ B,INS_ADM_CONFIRM C"
				+ " WHERE A.REGION_CODE='"
				+ parm.getValue("REGION_CODE")
				+ "' "
				+ " AND A.IN_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "000000"
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ parm.getValue("END_DATE")
				+ "235959"
				+ "','YYYYMMDDHH24MISS') "
				+ " AND B.CTZ_CODE=A.CTZ1_CODE "
				+
				// ��ݼ���Ⱥ����
				" AND B.NHI_CTZ_FLG = 'Y'"
				+ " AND B.CTZ_CODE IN("+parm.getValue("INS_CROWD_TYPE")//����д�� 21.��ְ��ͨ22.��ְ���� 23.�Ǿ�����
				+ ") AND C.CASE_NO = A.CASE_NO "
				+ // ���ҽ������������CASE_NO
				" AND (C.IN_STATUS IN ('0','1','7') OR C.IN_STATUS IS NULL) "+singleSql;
		//System.out.println("SQL:::::"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		return result;
	}
	/**
	 * ���÷ָ� ��ѯ����������Ϣ����
	 * @param parm
	 * @return
	 */
	public TParm queryConfirmInfo(TParm parm){
		TParm result = query("queryConfirmInfo", parm);
		return result;
	}
	/**
	 * У���Ƿ��������
	 * @param parm
	 * @return
	 */
	public TParm queryCheckAdmComfirm(TParm parm){
		TParm result = query("queryCheckAdmComfirm", parm);
		return result;
	}
	/**
	 * �����������
	 * @param parm
	 * @return
	 */
	public TParm insertDownLoadAdmConfirm(TParm parm, TConnection conn){
		TParm result = update("insertDownLoadAdmConfirm", parm,conn);
		return result;
	}
	/**
	 * �޸���Ժ״̬
	 * @param parm
	 * @param conn
	 * @return
	 */
	public TParm updateAdmConfrimForInStatus(TParm parm, TConnection conn){
		TParm result = update("updateAdmConfrimForInStatus", parm);
		return result;
	}
	/**
	 * ����渶���滼�߲�ѯ
	 * @param parm
	 * @return
	 */
	public TParm INS_DF_Seq(TParm parm) {
		String SQL = "SELECT A.ADM_SEQ, A.MR_NO, A.CASE_NO, A.ID_NO, A.PAT_NAME, A.COM_NAME, " +
				" (SELECT S.CTZ_DESC FROM SYS_CTZ S" +
				" WHERE S.CTZ_CODE = A.CTZ_TYPE) AS CTZ_DESC, A.IN_DATE, A.DS_DATE, A.REG_CASE_NO, " +
				" A.REG_TOT_AMT, " +
				" CASE A.ADVANCE_TYPE " +
				" WHEN '01' THEN '�����渶' WHEN '02' THEN '�����յ渶' ELSE '' END  AS ADVANCE_TYPE, " +
				" A.TOT_AMT, A.NHI_AMT, A.OWN_AMT, A.ADD_AMT, " +
				" CASE A.INS_STATUS " +
				" WHEN '1' THEN '�����ص渶˳���' WHEN '2' THEN '�ѷָ�' WHEN '3' THEN '���ϴ�' WHEN '4' THEN '�ѳ���' ELSE '' END AS INS_STATUS, " +
				" A.UPLOAD_DATE, A.CONTACT_ADDR, A.CONTACT_TYPE, A.CONTACTS FROM INS_ADVANCE_PAYMENT A " +
				" WHERE A.IN_DATE BETWEEN TO_DATE ('"+parm.getValue("START_DATE")+"000000','YYYYMMDDhh24miss') " +
				"AND TO_DATE ('"+parm.getValue("END_DATE")+"235959', 'YYYYMMDDhh24miss') AND A.INS_STATUS IN ('1', '2', '4')";
		//System.out.println("SQL:::::"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		return result;
	}
	/**
	 * ����INS_ADVANCE_PAYMENT
	 * @param parm
	 * @return
	 */
	public TParm insertAdvancePayment(TParm parm){
		String adm_no = parm.getValue("CONFIRM_NO");
		String sql = "select ADM_SEQ from INS_ADVANCE_PAYMENT where ADM_SEQ = '" +adm_no + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
			err("ERR:M " + result.getErrCode() + result.getErrText() +
		                result.getErrName());
			return result;
		}
		if(result.getCount()<=0){
			sql = "INSTER INTO INS_ADVANCE_PAYMENT(ADM_SEQ ,MR_NO ,CASE_NO ," +
                    "ID_NO ,PAT_NAME ,COM_NAME ,CTZ_TYPE ,IN_DATE ,DS_DATE ," +
                    "REG_CASE_NO ,REG_TOT_AMT ,ADVANCE_TYPE ,TOT_AMT ,NHI_AMT ," +
                    "OWN_AMT ,ADD_AMT ,INS_STATUS,CONTACT_ADDR,CONTACT_TYPE,CONTACTS,OPT_USER,OPT_DATE,OPT_TERM)" +
                    "VALUES('" + adm_no + "', " +
                    "(SELECT A.MR_NO FROM ADM_INP A WHERE A.CASE_NO = '" + parm.getValue("CASE_NO") + "'), " +
                    "'" + parm.getValue("CASE_NO") + "', " +
                    "'" + parm.getValue("SID") + "', " +
                    "'" + parm.getValue("NAME") + "', " +
                    "'" + parm.getValue("WORK_DEPARTMENT") + "', " +
                    "'" + parm.getValue("CTZ_CODE") + "', " +
                    "TO_DATE('" + parm.getValue("IN_HOSP_DATE") + "','YYYYMMDDHH24MISS'), " +
                    "TO_DATE('" + parm.getValue("OUT_HOSP_DATE") + "','YYYYMMDDHH24MISS'), " +
                    "'" + parm.getValue("CASE_NO") + "', " +
                    parm.getValue("REG_TOT_AMT") + ", " +
                    "'" +  parm.getValue("ADVANCE_TYPE") + "', " + "0,0,0,0,'1'," +
                    "'" + parm.getValue("CONTACT_ADDR") + "'," +
                    "'" + parm.getValue("CONTACT_TYPE") + "'," +
                    "'" + parm.getValue("CONTACTS") + "'," +
                    "'" + parm.getValue("OPT_USER")+ "',"+
                    "SYSDATE,"+
                    "'" + parm.getValue("OPT_TERM")+ "'"+
                    ")";
			//System.out.println("sql============"+sql);
			result = new TParm(TJDODBTool.getInstance().update(sql));
			if(result.getErrCode()<0){
				err("ERR:M " + result.getErrCode() + result.getErrText() +
			                result.getErrName());
				return result;
			}
		}
		return result;
	}
	/**
	 * ����渶ת�걨
	 * @param parm
	 * @return
	 */
	public TParm changeReport(TParm parm,TConnection conn){
		int succuss = 0;//�ɹ���
		int faile = 0;//ʧ����
		String Type = parm.getValue("TYPE"); // H:�ֶ�,B:�Զ�
	    String Hosp_Area = parm.getValue("REGION_CODE");
	    String Opt_User = parm.getValue("OPT_USER");
	    String Opt_Term = parm.getValue("OPT_TERM");
	    String adm_seq = parm.getValue("ADM_SEQ"); //�ʸ�ȷ�����*Ϊ��Ͽ��껼��
	    String YM = parm.getValue("YEAR_MON"); //�ں�
	    String Case_No = parm.getValue("CASE_NO");
	    //�ɹ�����
	    int Success_Cont = 0;
	    //ʧ�ܱ���
	    int lost_Cont = 0;
	    //��ɾ����ת
	    TParm result = INSIbsOrderTool.getInstance().deleteINSIbsOrder(parm);
	    //System.out.println(result);
	    if (result.getErrCode() < 0) {
			return result;
		}
	  //��ȡסԺ��������
	    String sql =
	    	"SELECT AA.ORDER_CODE, AA.DATE1, AA.DATE2, AA.CHARGE_Q, AA.CHARGE_T, " +
	    	" AA.OWN_PRICE, AA.TOT_AMT, AA.OWN_AMT, AA.ADDPAY_AMT, AA.NHI_AMT, " +
	    	" CASE WHEN AA.CTZ_CODE = '01'  THEN K.ZFBL1 WHEN AA.CTZ_CODE = '02' " +
	    	" THEN K.ZFBL2 WHEN AA.CTZ_CODE = '03' THEN K.ZFBL3 ELSE K.ZFBL1 END AS CTZ_CODE,AA.OWN_RATE, " +
	    	" AA.DOSE_CODE, '' AS SPACE2, AA.ADD_FLG, AA.CASE_NO_SEQ, AA.DRUG_TYPE, '' AS SPACE1, " +
	    	" AA.BILL_NO, K.PZWH FROM (SELECT B.ORDER_CODE, TO_CHAR (B.BILL_DATE, 'YYYYMMDDHH24MISS') DATE1, " +
	    	" TO_CHAR (B.BILL_DATE, 'YYYYMMDD') DATE2, '' AS CHARGE_Q, " +
	    	"  '' AS CHARGE_T, B.OWN_PRICE, B.TOT_AMT, B.OWN_AMT, '' AS ADDPAY_AMT, " +
	    	"  B.NHI_PRICE*B.DOSAGE_QTY AS NHI_AMT, B.OWN_RATE, B.DOSE_CODE, '', '' AS ADD_FLG, " +
	    	" B.CASE_NO_SEQ, '' AS DRUG_TYPE, '' AS SPCAE3, A.BILL_NO, P.CTZ1_CODE CTZ_CODE " +
	    	"  FROM IBS_ORDM A, IBS_ORDD B, ADM_INP P, IBS_BILLM M " +
	    	" WHERE A.REGION_CODE = '"+Hosp_Area+
	    	"' AND A.CASE_NO = '"+Case_No+"' AND B.CASE_NO = A.CASE_NO AND B.CASE_NO_SEQ = A.CASE_NO_SEQ " +
	    	"  AND A.CASE_NO = P.CASE_NO AND M.REGION_CODE = A.REGION_CODE AND M.BILL_NO = A.BILL_NO " +
	    	" AND M.REFUND_BILL_NO IS NULL AND M.REFUND_FLG = 'N') AA " +
	    	" LEFT JOIN SYS_FEE S ON AA.ORDER_CODE = S.ORDER_CODE AND S.ACTIVE_FLG = 'Y' LEFT JOIN " +
	    	" (SELECT DISTINCT SFXMBM, ZFBL1, ZFBL2, ZFBL3,PZWH, KSSJ, JSSJ FROM INS_RULE ) K " +
	    	" ON S.NHI_CODE_I = K.SFXMBM AND AA.DATE1 BETWEEN K.KSSJ AND K.JSSJ " +
	    	" ORDER BY AA.ORDER_CODE, AA.CHARGE_T, AA.TOT_AMT";
	   // System.out.println("asdasdasdasdas==========================="+sql);
	    result = new TParm(TJDODBTool.getInstance().select(sql));
	    //System.out.println("��˹��ˮ���"+result);
	    if(result.getErrCode()<0){
	    	err("ERR:M " + result.getErrCode() + result.getErrText() +
	                result.getErrName());
		    return result;
	    }
//	    if (result.getCount()>0) {
//	        result.removeRow(0);
//	    }else{
//	    	return result;
//	    }
	    TParm orderParm = new TParm();
	    TParm orderFeeParm = new TParm();
	    for (int i = 0; i < result.getCount(); i++) {
			orderParm = result.getRow(i);
			String Order_Code = orderParm.getValue("ORDER_CODE");
			// ��ѯҽ�������Ƿ����
			sql = "SELECT  NHI_CODE_I,ORDER_DESC,SPECIFICATION,ADDPAY_AMT FROM SYS_FEE WHERE ORDER_CODE='"+Order_Code+"' AND ACTIVE_FLG='Y'";
			TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
			//System.out.println("��ѯҽ�������Ƿ����===="+sql);
			////system.out.println("��ѯҽ�������Ƿ����===="+result1);
		    if(result1.getErrCode()<0){
		    	err("ERR:M " + result.getErrCode() + result.getErrText() +
		                result.getErrName());
			    return result;
		    }	    
		    if (result1.getCount()<0) {
		    	String Msg = Order_Code + "�˱�ҽ������շѱ�׼����";
		    	//system.out.println(Msg);
		    	return null;
		    }
		    String Own_Rate = orderParm.getValue("OWN_AMT");
		      if (Own_Rate.equals("")) Own_Rate = "0";
		      orderFeeParm.addData("ORDER_CODE", Order_Code);
		      orderFeeParm.addData("NHI_CODE_I", result1.getData("NHI_CODE_I", 0));//ҽ��ҽ����
		      orderFeeParm.addData("ORDER_DESC", result1.getData("ORDER_DESC", 0));//����
		      orderFeeParm.addData("DESCRIPTION", result1.getData("DESCRIPTION", 0));//���
		      orderFeeParm.addData("SPACE1", orderParm.getValue("SPACE1"));//ͳ����
		      orderFeeParm.addData("DATE1", orderParm.getValue("DATE1"));//��������
		      orderFeeParm.addData("DATE2", orderParm.getValue("DATE2"));//��������ʱ��
		      orderFeeParm.addData("CHARGE_T", orderParm.getInt("CHARGE_T"));//����
		      orderFeeParm.addData("OWN_PRICE", orderParm.getDouble("OWN_PRICE"));//����
		      orderFeeParm.addData("TOT_AMT", orderParm.getDouble("TOT_AMT"));//�������
		      orderFeeParm.addData("OWN_AMT", orderParm.getDouble("OWN_AMT"));//�Էѽ��
		      orderFeeParm.addData("ADDPAY_AMT", orderParm.getDouble("ADDPAY_AMT"));//�������
		      orderFeeParm.addData("NHI_AMT", orderParm.getDouble("NHI_AMT"));//�걨���
		      orderFeeParm.addData("OWN_RATE", Own_Rate);//�Էѱ���
		      orderFeeParm.addData("DOSE_CODE", orderParm.getValue("DOSE_CODE"));//����
		      orderFeeParm.addData("SPACE2", orderParm.getValue("SPACE2"));//�ۼ�����ע��
		      orderFeeParm.addData("ADD_FLG", orderParm.getValue("ADD_FLG"));//����ҩƷע��
		      orderFeeParm.addData("CASE_NO_SEQ", orderParm.getValue("CASE_NO_SEQ"));//Case_No_Seq
		      orderFeeParm.addData("DRUG_TYPE", orderParm.getValue("DRUG_TYPE"));//Drug_Type
		      orderFeeParm.addData("BILL_NO", orderParm.getValue("BILL_NO"));//�ʵ���(Bill_No)
		      orderFeeParm.addData("PZWH", orderParm.getValue("PZWH"));//��׼�ĺ�
		}
	    //system.out.println("orderFeeParm====="+orderFeeParm);
	    TParm ordD = new TParm();
	    if(orderFeeParm.getCount("BILL_NO")>0){
	    	TParm par = new TParm();
	    	//ת��ȡֵ
	    	for (int i = 0; i < orderFeeParm.getCount("BILL_NO"); i++) {
	    		par = orderFeeParm.getRow(i);
	    		sql = 
	    			"SELECT A.ADDPAY_FLG, B.TJMD FROM (SELECT X.NHI_CODE_I,Y.ADDPAY_FLG,X.ORDER_CODE " +
	    			" FROM SYS_FEE X, INS_IBS_ORDER Y " +
	    			" WHERE X.ORDER_CODE = Y.ORDER_CODE) A LEFT OUTER JOIN " +
	    			" INS_RULE B ON A.NHI_CODE_I = B.SFXMBM WHERE A.ORDER_CODE = '"+par.getValue("ORDER_CODE")+"'";
	    		//system.out.println("ת��ȡֵ=============="+sql);
	    		TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
	    		//system.out.println("ת��ȡֵ=============="+result2);
	    		String ACCRUAL = "N";
	            String Nhi_Ord_Class_Code = "07";
	            if (result2.getCount()>0) {
	                ACCRUAL = result2.getData("ADDPAY_FLG", 0).toString();
	                Nhi_Ord_Class_Code = result2.getData("TJMD", 0).toString();
	              }
	            ordD.addData("REGION_CODE", Hosp_Area);
	            ordD.addData("ORIGION_YEAR_MON", " ");//ԭ�ں�
	            ordD.addData("CASE_NO", Case_No);//case_No
	            ordD.addData("SEQ", (i+1));//���
	            ordD.addData("ADM_SEQ", adm_seq);//����˳���
	            ordD.addData("NEW_ADM_SEQ", adm_seq);//�¾���˳���
	            ordD.addData("CENTER", "91"); //������
	            ordD.addData("DATE2", par.getValue("DATE2")); //��������ʱ��
	            ordD.addData("HOSP_NHI_NO", "000551"); //ҽԺ����
	            ordD.addData("ORDER_CODE", par.getValue("ORDER_CODE")); //ҽ�����
	            ordD.addData("NHI_CODE_I", par.getValue("NHI_CODE_I")); //����ҽ����
	            ordD.addData("ORDER_DESC", par.getValue("ORDER_DESC")); //ҽ��������
	            ordD.addData("OWN_RATE", par.getValue("OWN_RATE")); //�Ը�����
	            String Dose_Code = par.getValue("DOSE_CODE"); //���ʹ���
	            ordD.addData("DOSE_CODE", par.getValue("DOSE_CODE")); //���ʹ���
	            ordD.addData("DOSE_DESC", this.INS_PhaDose_Desc(Dose_Code)); //��������
	            ordD.addData("DESCRIPTION", par.getValue("DESCRIPTION")); //���
	            ordD.addData("OWN_PRICE", par.getDouble("OWN_PRICE")); //����
	            ordD.addData("CHARGE_T", par.getInt("CHARGE_T")); //����(����)
	            ordD.addData("TOT_AMT", par.getDouble("TOT_AMT")); //�������
	            ordD.addData("NHI_AMT", par.getDouble("NHI_AMT")); //�걨���
	            ordD.addData("OWN_AMT", par.getDouble("OWN_AMT")); //�Էѽ��
	            ordD.addData("ADDPAY_AMT", par.getDouble("ADDPAY_AMT")); //�������
	            //ͳ�ƴ���
	            Nhi_Ord_Class_Code = Nhi_Ord_Class_Code.equals("null") ? "07" :
	                                 Nhi_Ord_Class_Code;
	            ordD.addData("OP_FLG", Nhi_Ord_Class_Code.equals("04") ? "Y" : "N"); //������־
	            ordD.addData("ACCRUAL_FLG", ACCRUAL.equals("null") ? "N" : ACCRUAL); //�ۼ�������־
	            ordD.addData("NHI_ORD_CLASS_CODE", Nhi_Ord_Class_Code); //ͳ�ƴ���
	            //����ҩƷ��־ADD_FLG
	            ordD.addData("ADD_FLG", par.getValue("ADD_FLG").equals("")? "N" :par.getValue("ADD_FLG")); //����ҩƷ��־
	            String Drug_Type = par.getValue("DRUG_TYPE");
	            ordD.addData("CARRY_FLG", Drug_Type.equals("2") ? "Y" : "N"); //��Ժ��ҩ
	            ordD.addData("OPT_USER", Opt_User); 
	            ordD.addData("OPT_TERM", Opt_Term); //��Ժ��ҩ
	            ordD.addData("PZWH", par.getValue("PZWH")); //��׼�ĺ�
			}
	    	//system.out.println("ת��ȡֵ====="+ordD);
	    }
	  //�ȴ����n,�ٴ���ϸ��
	      if ( ordD.getCount("ACCRUAL_FLG")> 0) {
	        for (int k = 0; k < ordD.getCount("ACCRUAL_FLG"); k++) {
	          TParm ordD_Row = ordD.getRow(k);
	          result = this.INS_OrdD_Insert(ordD_Row,conn);
	          if(result.getErrCode()<0){
	        	  faile++;
	        	  err("ERR:M " + result.getErrCode() + result.getErrText() +
			                result.getErrName());
				    return result; 
	          }
	        }
	      }
	      TParm OrdDUnion = this.INS_OrdDUnion_Select_DF(Case_No, Hosp_Area); //�ں�
	      //system.out.println("OrdDUnion===="+OrdDUnion);
//	    	  OrdDUnion.removeRow(0);
	      if(OrdDUnion.getCount()>0){
	    	  result = this.INS_ORDD_UPLOAD_Delete(Hosp_Area, adm_seq, conn);
	    	  if(result.getErrCode()<0){
	    		  faile++;
	        	  err("ERR:M " + result.getErrCode() + result.getErrText() +
			                result.getErrName());
				    return result; 
	          }
	      }
	        for (int k = 0; k < OrdDUnion.getCount(); k++) {
	            Vector vOrdD_Row1 = new Vector();
	            vOrdD_Row1.add(OrdDUnion.getData("REGION_CODE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("YEAR_MON", k));
	            vOrdD_Row1.add(OrdDUnion.getData("ADM_SEQ", k));
	            vOrdD_Row1.add(OrdDUnion.getData("HOSP_NHI_NO", k));
	            vOrdD_Row1.add(OrdDUnion.getData("ORDER_CODE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("ORDER_DESC", k));
	            vOrdD_Row1.add(OrdDUnion.getData("DOSE_DESC", k));
	            vOrdD_Row1.add(OrdDUnion.getData("STANDARD", k));
	            vOrdD_Row1.add(OrdDUnion.getData("PHAADD_FLG", k));
	            vOrdD_Row1.add(OrdDUnion.getData("CARRY_FLG", k));
	            vOrdD_Row1.add(OrdDUnion.getData("PRICE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("SUM(A.QTY)", k));
	            vOrdD_Row1.add(OrdDUnion.getData("SUM(A.TOTAL_AMT)", k));
	            vOrdD_Row1.add(OrdDUnion.getData("SUM(A.TOTAL_NHI_AMT)", k));
	            vOrdD_Row1.add(OrdDUnion.getData("SUM(A.OWN_AMT)", k));
	            vOrdD_Row1.add(OrdDUnion.getData("SUM(A.ADDPAY_AMT)", k));
	            vOrdD_Row1.add(OrdDUnion.getData("OP_FLG", k));
	            vOrdD_Row1.add(OrdDUnion.getData("ADDPAY_FLG", k));
	            vOrdD_Row1.add(OrdDUnion.getData("NHI_ORD_CLASS_CODE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("PHAADD_FLG_1", k));
	            vOrdD_Row1.add(OrdDUnion.getData("CARRY_FLG_1", k));
	            vOrdD_Row1.add(OrdDUnion.getData("NHI_ORDER_CODE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("SPACE1", k));
	            vOrdD_Row1.add(OrdDUnion.getData("NHI_CODE_I", k));
	            vOrdD_Row1.add(OrdDUnion.getData("OWN_PRICE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("OWN_RATE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("DOSE_CODE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("BILL_DATE", k));
	            vOrdD_Row1.add(OrdDUnion.getData("HYGIENE_TRADE_CODE", k));
	            String order = String.valueOf(vOrdD_Row1.get(4));
	            TParm Vc = this.INS_OrdDTime_Select(Case_No, order);
//	            Vc.removeRow(0);
	            vOrdD_Row1.add(  Vc.getData("MAX(OPT_USER)", 0)); //Opt_User28
	            vOrdD_Row1.add(Vc.getData("MAX(OPT_DATE)", 0)); //Opt_Date29
	            vOrdD_Row1.add(Vc.getData("MAX(OPT_TERM)", 0)); //Opt_Term30
	            vOrdD_Row1.add(String.valueOf(k + 1)); //seq_no31
	            if (!vOrdD_Row1.get(11).equals("0")) {
	              //Insert INS_ORDD_UPLOAD(ת��ʹ��)
	              result = this.INS_ORDD_UPLOAD_Insert(vOrdD_Row1,conn);
	              if(result.getErrCode()<0){
	            	  faile++;
	            	  return result;
	              }
	              succuss++;
	            }
	          }
	        result.setData("ERROR_INDEX", faile);
	        result.setData("SUCCESS_INDEX", succuss);
		return result;

	}
	/**
	 * ��������
	 * @param Dose_Code
	 * @return
	 */
	  public String INS_PhaDose_Desc( String Dose_Code) {
	    String Dose_Desc = "";
	    String sql = "SELECT DOSE_CHN_DESC FROM PHA_DOSE" +
	    		" WHERE DOSE_CODE='"+Dose_Code+"'";
	    TParm result = new TParm(TJDODBTool.getInstance().select(sql));
	    if(result.getCount()>0){
	    	Dose_Desc = result.getData("DOSE_CHN_DESC", 0).toString();
	    }
	    return Dose_Desc;
	  }
	  /**
	   * Insert OrdD(ת��ʹ��)
	   * @param parm
	   * @param conn
	   */
	  public TParm INS_OrdD_Insert(TParm parm,TConnection conn) {
		  String sql =
			  "INSERT INTO INS_IBS_ORDER " +
			  " (REGION_CODE, YEAR_MON, CASE_NO, SEQ_NO, ADM_SEQ, NEWADM_SEQ, " +
			  " INSBRANCH_CODE, BILL_DATE, HOSP_NHI_NO, ORDER_CODE, " +
			  " NHI_ORDER_CODE, ORDER_DESC, OWN_RATE, DOSE_CODE, DOSE_DESC, " +
			  " STANDARD, PRICE, QTY, TOTAL_AMT, TOTAL_NHI_AMT, OWN_AMT, " +
			  " ADDPAY_AMT, OP_FLG, ADDPAY_FLG, NHI_ORD_CLASS_CODE, PHAADD_FLG, " +
			  " CARRY_FLG, OPT_USER, OPT_DATE, OPT_TERM, HYGIENE_TRADE_CODE) " +
			  " VALUES ('"+parm.getValue("REGION_CODE")+"', '"+parm.getValue("ORIGION_YEAR_MON")+"', '"+parm.getValue("CASE_NO")+"', " +
			  " '"+parm.getValue("SEQ")+"', '"+parm.getValue("ADM_SEQ")+"', '"+parm.getValue("NEW_ADM_SEQ")+"', " +
			  " '"+parm.getValue("CENTER")+"', TO_DATE ('"+parm.getValue("DATE2")+"000000', 'YYYYMMDDhh24miss'), " +
			  " '"+parm.getValue("HOSP_NHI_NO")+"', '"+parm.getValue("ORDER_CODE")+"', " +
			  " '"+parm.getValue("NHI_CODE_I")+"', '"+parm.getValue("ORDER_DESC")+"', '"+parm.getDouble("OWN_RATE")+"', " +
			  " '"+parm.getValue("DOSE_CODE")+"', '"+parm.getValue("DOSE_DESC")+"', " +
			  " '"+parm.getValue("DESCRIPTION")+"', '"+parm.getInt("OWN_PRICE")+"', '"+parm.getValue("CHARGE_T")+"', " +
			  " '"+parm.getDouble("TOT_AMT")+"', '"+parm.getDouble("NHI_AMT")+"', '"+parm.getDouble("OWN_AMT")+"', " +
			  " '"+parm.getDouble("ADDPAY_AMT")+"', '"+parm.getValue("OP_FLG")+"', " +
			  " '"+parm.getValue("ACCRUAL_FLG")+"', '"+parm.getValue("NHI_ORD_CLASS_CODE")+"', '"+parm.getValue("ADD_FLG")+"', " +
			  " '"+parm.getValue("CARRY_FLG")+"', '"+parm.getValue("OPT_USER")+"', SYSDATE, " +
			  " '"+parm.getValue("OPT_TERM")+"', '"+parm.getValue("PZWH")+"')";
		  TParm result  = new TParm(TJDODBTool.getInstance().update(sql,conn));
		  if(result.getErrCode()<0){
			  err("ERR:M " + result.getErrCode() + result.getErrText() +
		                result.getErrName());
			    return result; 
		  }
		  return result; 
	  }
	  /**
	   * ����渶�ָ�ת�걨׼�����ݲ�ѯ����֮һ
	   * @param Case_No
	   * @param Hosp_Area
	   * @return
	   */
	  public TParm INS_OrdDUnion_Select_DF(String Case_No, String Hosp_Area){
		  String sql = 
			  "SELECT   A.REGION_CODE, A.YEAR_MON, A.ADM_SEQ, A.HOSP_NHI_NO, A.ORDER_CODE, " +
			  " A.ORDER_DESC, A.DOSE_DESC, A.STANDARD, A.PHAADD_FLG, A.CARRY_FLG, " +
			  " A.PRICE, SUM (A.QTY), SUM (A.TOTAL_AMT), SUM (A.TOTAL_NHI_AMT), " +
			  " SUM (A.OWN_AMT), SUM (A.ADDPAY_AMT), A.OP_FLG, A.ADDPAY_FLG, " +
			  " A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG, A.NHI_ORDER_CODE, " +
			  " '0' AS SPACE1, C.NHI_CODE_i, C.OWN_PRICE, A.OWN_RATE, A.DOSE_CODE, " +
			  " MAX (A.BILL_DATE) BILL_DATE, A.HYGIENE_TRADE_CODE " +
			  " FROM INS_IBS_ORDER A, SYS_FEE C " +
			  " WHERE A.REGION_CODE = '"+Hosp_Area+"' " +
			  " AND CASE_NO = '"+Case_No+"' " +
			  " AND A.TOTAL_AMT <> 0 " +
			  " AND A.ORDER_CODE = C.ORDER_CODE " +
			  " AND C.ACTIVE_FLG ='Y' " +
			  " GROUP BY A.REGION_CODE,A.YEAR_MON,A.ADM_SEQ,A.HOSP_NHI_NO,A.ORDER_CODE,A.ORDER_DESC," +
			  "A.DOSE_DESC,A.STANDARD,A.PHAADD_FLG,A.CARRY_FLG,A.PRICE,A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE," +
			  "A.PHAADD_FLG,A.CARRY_FLG,A.NHI_ORDER_CODE,C.NHI_CODE_i,C.OWN_PRICE,A.OWN_RATE,A.DOSE_CODE," +
			  "A.HYGIENE_TRADE_CODE";
		  //system.out.println(sql);
		  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		  if(result.getErrCode()<0){
			  err("ERR:M " + result.getErrCode() + result.getErrText() +
		                result.getErrName());
			    return result; 
		  }
		  return result;
	  }
	  /**
	   * Delete INS_ORDD_UPLOAD(ת��ʹ��)
	   * @param Hosp_Area
	   * @param Case_No
	   * @param conn
	   */
	  public TParm INS_ORDD_UPLOAD_Delete(String Hosp_Area, String Adm_Seq,
	                                     TConnection conn){
		  String sql =
			  "DELETE FROM INS_IBS_UPLOAD WHERE REGION_CODR='"+Hosp_Area+"' AND ADM_SEQ='"+Adm_Seq+"'";
		  TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		  return result;
	  }
	  /**
	   * ����渶�ָ�ת�걨׼�����ݲ�ѯ����֮һ
	   * @param caseno
	   * @param order
	   * @return
	   */
	  public TParm INS_OrdDTime_Select(String caseno, String order){
		  String sql =
			  " SELECT MIN(BILL_DATE),OPT_USER,OPT_DATE,OPT_TERM FROM INS_IBS_ORDER " +
			  " WHERE CASE_NO='" + caseno + "'" +
			  " AND ORDER_CODE='" + order + "' GROUP BY OPT_USER,OPT_DATE,OPT_TERM";
		  TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		  if(result.getErrCode()<0){
			  err("ERR:M " + result.getErrCode() + result.getErrText() +
		                result.getErrName());
			    return result; 
		  }
		  return result;

  }
	  /**
	   * Insert INS_ORDD_UPLOAD(ת��ʹ��)
	   * @param vOrdD
	   * @param SEQ_NO
	   */
	  public TParm INS_ORDD_UPLOAD_Insert(Vector vOrdD,TConnection conn){
//	    //system.out.println("vOrdDlish" + vOrdD);
//	    //system.out.println("vOrdD.get(29)" + vOrdD.get(29));
//	    //system.out.println("vOrdD.get(27)" + vOrdD.get(27));
	    String PhaAdd_flg = String.valueOf(vOrdD.get(17)).equals("null") ? "N" :
	                        String.valueOf(vOrdD.get(17));
	    String Dose_Code = String.valueOf(vOrdD.get(26)).equals("null") ? "" :
	                       String.valueOf(vOrdD.get(26));
	    String Standard = String.valueOf(vOrdD.get(7)).equals("null") ? "" :
	                      String.valueOf(vOrdD.get(7));
	    double Total_Nhi_Amt = String.valueOf(vOrdD.get(13)).equals("") ? 0 :
	                           Double.parseDouble(String.valueOf(vOrdD.get(13)));
	    double Own_Amt = String.valueOf(vOrdD.get(14)).equals("") ? 0 :
	                     Double.parseDouble(String.valueOf(vOrdD.get(14)));
	    double AddPay_Amt = String.valueOf(vOrdD.get(15)).equals("") ? 0 :
	                        Double.parseDouble(String.valueOf(vOrdD.get(15)));
	    String date = vOrdD.get(27).toString();
	    date = date.substring(0, 4) + date.substring(5, 7) +
	    date.substring(8, 10) + date.substring(11, 13) +
	    date.substring(14, 16) + date.substring(17, 19);
	    String sql = " INSERT INTO INS_IBS_UPLOAD(" +
			         " REGION_CODE," +
			         " SEQ_NO,ADM_SEQ," +
			         " CHARGE_DATE," +
			         " ORDER_CODE,NHI_ORDER_CODE,ORDER_DESC," +
			         " OWN_RATE,DOSE_CODE," +
			         " STANDARD,PRICE,QTY," +
			         " TOTAL_AMT,TOTAL_NHI_AMT,OWN_AMT," +
			         " ADDPAY_AMT,OP_FLG,ADDPAY_FLG," +
			         " NHI_ORD_CLASS_CODE,PHAADD_FLG,CARRY_FLG," +
			         " OPT_USER,OPT_DATE,OPT_TERM,HYGIENE_TRADE_CODE)" + // ��д��׼�ĺ�
			         " VALUES(" +
	                 " '" + vOrdD.get(0) + "'," +
	                 " '" + vOrdD.get(32) + "','" + vOrdD.get(2) + "'," +
	                 " TO_DATE('" + date + "','YYYYMMDDHH24MISS')," +

	                 " '" + vOrdD.get(4) + "' , '" + vOrdD.get(21) + "' , '" +
	                 vOrdD.get(5) +
	                 "'," +
	                 " " + vOrdD.get(25) + ", '" + Dose_Code +
	                 "', " +
	                 " '" + Standard + "'," + vOrdD.get(10) + "," + vOrdD.get(11) +
	                 " ," +
	                 " " + vOrdD.get(12) + ", " + Total_Nhi_Amt + " , " + Own_Amt +
	                 " ," +
	                 " " + AddPay_Amt + ",'" + vOrdD.get(16) + "','" + PhaAdd_flg +
	                 "'," +
	                 " '" + vOrdD.get(18) + "','" + vOrdD.get(19) + "','" +
	                 vOrdD.get(20) +
	                 "'," +
	                 " '" + vOrdD.get(29) + "',SYSDATE," +

	                 "'" + vOrdD.get(31) + "','" + vOrdD.get(28) + "')"; // ��д��׼�ĺ�
	    //system.out.println(sql);
	    TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
		  if(result.getErrCode()<0){
			  err("ERR:M " + result.getErrCode() + result.getErrText() +
		                result.getErrName());
			    return result; 
		  }
	    return result;
	  }
	  /**
	   * �޸ĳ�Ժ��Ϣ
	   * @param parm
	   * @return
	   * =====pangben 2012-3-16
	   */
	  public TParm updatedDsDiag(TParm parm){
		  TParm result = update("updatedDsDiag", parm);
		  return result;
	  }
}

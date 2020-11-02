package jdo.ibs;

import java.sql.Timestamp;

import jdo.bil.BIL;
import jdo.mem.MEMTool;
import jdo.odi.OdiMainTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
/**
*
* <p>Title: �ײ�ҹ�����ο�����</p>
*
* <p>Description:�ײ�ҹ�����ο����� ������������</p>
*
* <p>Copyright: Copyright (c) Liu dongyang 2015</p>
*
* <p>Company: bluecore</p>
*
* @author pangben
* @version 1.0
* 
*/
public class IBSLumpWorkBatchNewTool extends TJDOTool{
	/**
     * ʵ��
     */
    public static IBSLumpWorkBatchNewTool instanceObject;
    /**
     * �õ�ʵ��
     * @return IBSTool
     */
    public static IBSLumpWorkBatchNewTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IBSLumpWorkBatchNewTool();
        return instanceObject;
    }
    public TParm onLumpWorkBatch(TParm admParm,TConnection connection){
    	String statCode=OdiMainTool.getInstance().getOdiSysParmData("ODI_STAT_CODE").toString();//��ʱҽ��Ƶ��
    	TParm result=new TParm();
    	if (admParm.getErrCode()<0) {
    		System.out.println("��ѯ������Ϣ��������:"+admParm.getErrText());
    		result.setErr(-1,"��ѯ������Ϣ��������:"+admParm.getErrText());
    		//this.setMessage();
			return result;
		}
    	if (admParm.getValue("CASE_NO").length()<=0) {
    		result.setData("MESSAGE","û����Ҫ����������");
    		//result.setErr(-1,"û����Ҫ����������");
    		//this.setMessage("û����Ҫ����������");
    		return result;
		}
    	if(null==admParm.getValue("LUMPWORK_RATE")||
    			admParm.getValue("LUMPWORK_RATE").length()<=0||admParm.getDouble("LUMPWORK_RATE")==0.00){
    		result.setErr(-1,"������:"+admParm.getValue("MR_NO")+"δ�����ײ��ۿ�");
    		return result;
    	}
    	TParm odiParm = new TParm(TJDODBTool.getInstance().select("SELECT LUMPWORK_ORDER_CODE FROM ODI_SYSPARM"));
    	if (odiParm.getValue("LUMPWORK_ORDER_CODE",0).length()<=0) {
    		result.setErr(-1,"�ײͲ���ҽ���ֵ����ô���");
    		return result;
		}
    	//TConnection connection= TDBPoolManager.getInstance().getConnection();
    	String caseNo="";
    	//String mrNo="";
    	String packCode="";
    	TParm noExeBillParm=null;//δ����ҽ��
    	TParm memPatPackAgeSectionParm=null;
    	double diffQty=0.00;
    	TParm numberParm=null;
    	String mCaseNo="";
    	String sumCaseNo="";
    	String caseNoNew="";//�ײͲ�������ĸ�׾���Ų�ѯ�ײ�������
		caseNo=admParm.getValue("CASE_NO");
		caseNoNew=admParm.getValue("CASE_NO");
		if (admParm.getValue("NEW_BORN_FLG").equals("Y")) {//������ǰ����������������
			mCaseNo=admParm.getValue("M_CASE_NO");
			caseNoNew=admParm.getValue("M_CASE_NO");
			String admSql="SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+mCaseNo+"' AND DS_DATE IS NULL AND CANCEL_FLG<>'Y'";
			TParm caseParm=new TParm(TJDODBTool.getInstance().select(admSql));
			if (caseParm.getCount()>0) {
				for (int j = 0; j < caseParm.getCount(); j++) {
					sumCaseNo+="'"+caseParm.getValue("CASE_NO",j)+"',";
				}
				sumCaseNo+="'"+mCaseNo+"'";
			}
		}else{
			mCaseNo="";
			String admSql="SELECT CASE_NO FROM ADM_INP WHERE M_CASE_NO='"+caseNo+"' AND DS_DATE IS NULL AND CANCEL_FLG<>'Y'";
			TParm caseParm=new TParm(TJDODBTool.getInstance().select(admSql));
			if (caseParm.getCount()>0) {
				for (int j = 0; j < caseParm.getCount(); j++) {
					sumCaseNo+="'"+caseParm.getValue("CASE_NO",j)+"',";
				}
				sumCaseNo+="'"+caseNo+"'";
			}
		}
		
		//mrNo=admParm.getValue("MR_NO");
		packCode=admParm.getValue("LUMPWORK_CODE");//�ײʹ���
		noExeBillParm=getNoIncludeExecFlgBill(caseNo);//���ݾ���Ų�ѯδ����ļƷ�ҽ������
		if (noExeBillParm.getErrCode()<0) {
			System.out.println("��ѯδ����ҽ����Ϣ��������:"+noExeBillParm.getErrText());
			result.setErr(-1,"��ѯδ����ҽ����Ϣ��������:"+noExeBillParm.getErrText());
			return result;
		}
		int seqNo = 1;
		numberParm=new TParm();
		String groupSql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD"
			+ "  WHERE CASE_NO = '" + caseNo + "'";
		TParm maxGroupNoParm = new TParm(TJDODBTool.getInstance().select(groupSql));
		if (maxGroupNoParm.getErrCode() < 0) {
			return maxGroupNoParm;
		}
		TParm maxCaseNoSeqParm = selMaxCaseNoSeq(caseNo);
		numberParm.setData("CASE_NO_SEQ",maxCaseNoSeqParm.getInt("CASE_NO_SEQ",0)+1);
		if (maxGroupNoParm.getCount()<=0||null==maxGroupNoParm.getValue("ORDERSET_GROUP_NO",0)||
				maxGroupNoParm.getValue("ORDERSET_GROUP_NO",0).length()<=0) {
			numberParm.setData("GROUP_NO",1);
		}else{
			numberParm.setData("GROUP_NO",maxGroupNoParm.getInt("ORDERSET_GROUP_NO",0)+1);
		}
		for (int j = 0; j < noExeBillParm.getCount("ORDER_CODE"); j++) {
			if (noExeBillParm.getValue("ORDER_CODE",j).equals(odiParm.getValue("LUMPWORK_ORDER_CODE",0))) {
				//�Ѿ��������ԺȻ������޸���ݽ����ײ����β�����ҽ��������
				continue;
			}
			if (noExeBillParm.getDouble("DOSAGE_QTY",j)==0) {//����0������
				continue;
			}
//			if(noExeBillParm.getValue("CAT1_TYPE",j).equals("PHA")){//ҩƷ�Ѳ���������
//				continue;		
//			}
//			if(noExeBillParm.getValue("CHARGE_HOSP_CODE",j).equals("RA")){//Ѫ�Ѳ���������
//				continue;		
//			}
    		numberParm.setData("SEQ_NO",seqNo);
//			if (noExeBillParm.getDouble("DOSAGE_QTY",j)<=0) {
//				continue;
//			}
			if (mCaseNo.length()>0) {
				memPatPackAgeSectionParm=getMemPatPackAgeSection(mCaseNo, packCode, noExeBillParm.getValue("ORDER_CODE",j));
			}else{
				memPatPackAgeSectionParm=getMemPatPackAgeSection(caseNo, packCode, noExeBillParm.getValue("ORDER_CODE",j));
			}
			if (memPatPackAgeSectionParm.getErrCode()<0) {
    			System.out.println("��ѯ�ײ���ϸҽ����Ϣ��������:"+memPatPackAgeSectionParm.getErrText());
    			result.setErr(-1,"��ѯ�ײ���ϸҽ����Ϣ��������:"+memPatPackAgeSectionParm.getErrText());
    			return result;
			}
			TParm parmIbsOrdd=getIbsOrddOrderParm(caseNo, noExeBillParm.getValue("ORDER_CODE",j));
			if (memPatPackAgeSectionParm.getCount()<=0) {//�ײ���ϸ�в����ڣ���ҽ��Ϊ�ײ���
				result=onSaveExeIbsOrdd(parmIbsOrdd,caseNo, noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
						j, admParm, numberParm, connection,statCode,caseNoNew);
				if (result.getErrCode()<0) {
					System.out.println("�ײ�������ӽ��ױ��������:"+result.getErrText());
					return result;
				}
			}else{
				if (memPatPackAgeSectionParm.getValue("UN_NUM_FLG",0).equals("Y")) {//pangben 2015-9-2����������
					continue;
				}
				double orderSum=memPatPackAgeSectionParm.getDouble("ORDER_NUM",0);//�ײ�ҽ����ϸ�л�������
				double execDosageQty=0.00;
				double checkDosageQty=0.00;//У�鵱ǰ�������˲�������������Ϣ�����ױ����Ƿ��������ҽ��
				if (noExeBillParm.getDouble("DOSAGE_QTY",j)<0) {//����С��0����
					if (sumCaseNo.length()>0) {
						execDosageQty=getOnExeUnIncludeExecFlgBill(sumCaseNo,  noExeBillParm.getValue("ORDER_CODE",j));//�Ѿ������ҽ������
					}else{
						execDosageQty=getOnExeUnIncludeExecFlgBill("'"+caseNo+"'",  noExeBillParm.getValue("ORDER_CODE",j));//�Ѿ������ҽ������
					}
					if (execDosageQty!=0) {//����û�мƷѵ�ҽ����˵�����˷�ҽ��Ϊ����ҽ����ֱ�Ӹ���״̬
						checkDosageQty=getOnExeUnIncludeExecFlgBill("'"+caseNo+"'",  noExeBillParm.getValue("ORDER_CODE",j));//�Ѿ������ҽ������
						if(checkDosageQty==0){
							continue;
						}
					}
		    		//=====ҩ����ҩ����ʿȡ��ִ�и�����������������������ҽ��ȡ��������ȡ��������סԺ�Ƽ۽������
					result=getExeUnBill(orderSum, execDosageQty, diffQty, noExeBillParm, j, 
							parmIbsOrdd, caseNo, admParm, numberParm, connection, statCode,caseNoNew);
				}else if(noExeBillParm.getDouble("DOSAGE_QTY",j)>0){//����������0����
					if (sumCaseNo.length()>0) {
						execDosageQty=getOnExeIncludeExecFlgBill(sumCaseNo,  noExeBillParm.getValue("ORDER_CODE",j));//�Ѿ������ҽ������
					}else{
						execDosageQty=getOnExeIncludeExecFlgBill("'"+caseNo+"'",  noExeBillParm.getValue("ORDER_CODE",j));//�Ѿ������ҽ������
					}
					result=getExeBill(orderSum, execDosageQty, diffQty, noExeBillParm, j,
							parmIbsOrdd, caseNo, admParm, numberParm, connection, statCode,caseNoNew);
				}
				if (null==result) {
					continue;
				}
				if (result.getErrCode()<0) {
					System.out.println("�ײ�������ӽ��ױ��������:"+result.getErrText());
					return result;
				}
			}
			result=onSaveIbsOrdm(parmIbsOrdd.getRow(0), numberParm, caseNo, connection);
			if (result.getErrCode()<0) {
				System.out.println("�ײ�������ӽ��ױ��������:"+result.getErrText());
				return result;
			}
			numberParm.setData("CASE_NO_SEQ",numberParm.getInt("CASE_NO_SEQ")+1);//����
		}
		//����IBS_ORDD ��
		String sql="UPDATE IBS_ORDD SET INCLUDE_EXEC_FLG='Y' WHERE CASE_NO='"+caseNo+"'";
		result=new TParm(TJDODBTool.getInstance().update(sql,connection));
		if (result.getErrCode()<0) {
			System.out.println("�ײ�������ӽ��ױ��������:"+result.getErrText());
			return result;
		}
    	return result;
    }
    /**
     * ִ��״̬�޸�  BILL_EXE_FLG
     * @param caseNo
     * @param ordersetFlg
     * @param orderCode
     * @param connection
     * @return
     */
    private TParm getExeBillExeFlg(String caseNo,boolean ordersetFlg,String orderCode,TConnection connection){
    	String sql="UPDATE IBS_ORDD SET BILL_EXE_FLG='Y' WHERE " +
    			"CASE_NO='"+caseNo+"' AND (INCLUDE_EXEC_FLG='N' OR INCLUDE_EXEC_FLG IS NULL) AND" +
    					" (ORDERSET_CODE IS NULL OR ORDERSET_CODE=ORDER_CODE) AND ORDER_CODE='"+orderCode+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().update(sql,connection));
    	if (result.getErrCode()<0) {
			System.out.println("�ײ������޸�ִ��״̬��������:"+result.getErrText());
			return result;
		}
    	if(ordersetFlg){//����ҽ��
    		sql="UPDATE IBS_ORDD SET BILL_EXE_FLG='Y' WHERE " +
        			"CASE_NO='"+caseNo+"' AND (INCLUDE_EXEC_FLG='N' OR INCLUDE_EXEC_FLG IS NULL) AND" +
        					" ORDERSET_CODE='"+orderCode+"'";
        	result=new TParm(TJDODBTool.getInstance().update(sql,connection));
        	if (result.getErrCode()<0) {
    			System.out.println("�ײ������޸�ִ��״̬��������:"+result.getErrText());
    			return result;
    		}
    	}
    	return result;
    }
    /**
     * 
    * @Title: getExeBill
    * @Description: TODO(�����ײ�ҽ���շѲ����߼�)
    * @author pangben
    * @param orderSum �ײ�����
    * @param execDosageQty �Ѿ������ҽ������
    * @param diffQty
    * @param noExeBillParm
    * @param j
    * @param parmIbsOrdd
    * @param caseNo
    * @param admParm
    * @param i
    * @param numberParm
    * @param connection
    * @param statCode
    * @return
    * @throws
     */
    private TParm getExeBill(double orderSum,double execDosageQty,double diffQty,
    		TParm noExeBillParm,int j,TParm parmIbsOrdd,String caseNo,
    		TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
    	//�Ƚ�ͳ�Ƶ��������ײ���ϸ�л��ܳ���ҽ������
		if (orderSum>=execDosageQty) {//����ײ���ϸҽ���������ڵ�ǰ�Ѿ��������ҽ������
			diffQty =orderSum- execDosageQty;//�����������	
			if (diffQty>=noExeBillParm.getDouble("DOSAGE_QTY",j)) {//��������뵱ǰ�����ļƷ�ҽ�������Ƚ�
				//�ײ���ҽ��������״̬
				//diffQty= diffQty -execDosageQty ;//ͳ������
				//result=null;
				return null;
			}else{
				if(diffQty>0){//�����ײ���ҽ��������ҽ��
					//���˼Ʒ�ҽ�������ֿ�
					//1.	���˲���ҽ���帺���ײ�״̬Ϊ�ײ���
					//2.	���һ���ײ���ҽ��������ΪDIFF_QTY���������������¼��㣨���ݵ�һ���ȷ�Ͻ��Ʒ�ʱ��Ϊ��ǰʱ�䣩
					//3.	���һ���������ݣ�����ΪDOSAGE_QTY -DIFF_QTY���ײ�״̬Ϊ�ײ��⣬
					//�������������¼��㣨���ݵ�һ��ݺ͵ڶ����ȷ�Ͻ��Ʒ�ʱ��Ϊ��ǰʱ�䣩
					//���磺
					//�ײ͹����ֵ� ע���� ����Ϊ10ֻ
					//�Ʒѱ���  ע�����Ѿ����� 9ֻ 
					//7/8 �� �����Ʒ�  ע���� 3ֻ
					//��Ҫ����������
					//����       �ײ�״̬     �Ʒ�ʱ��
					//-3           �ײ���              7/8 ��    �帺ʹ��
					// 1           �ײ���              7/8 ��    ����10ֻ�ײ���
					// 2           �ײ���              7/8 ��    �����ײ�������
					//�ײ�������
					result=onSaveExeIbsOrddOne(parmIbsOrdd,caseNo, noExeBillParm.getValue("ORDER_CODE",j),diffQty,noExeBillParm.getDouble("DOSAGE_QTY",j),
    						 admParm, numberParm, connection,statCode,caseNoNew);
    				if (result.getErrCode()<0) {
    					return result;
					}
				}else{//diffQty<=0����  
					//�ײ���ҽ�����ã�����ҽ���帺���ײ�״̬Ϊ�ײ��ڣ�
					//�������һ���ײ������ݣ�ʵ�ս�����������¼��㣨���ݵ�һ��ݺ͵ڶ����ȷ�Ͻ��Ʒ�ʱ��Ϊ��ǰʱ�䣩
					result=onSaveExeIbsOrdd(parmIbsOrdd,caseNo,noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
    						j, admParm, numberParm, connection,statCode,caseNoNew);
    				if (result.getErrCode()<0) {
    					return result;
					}
				}
			}
		}else{
			//�ײ���ҽ�����ã�����ҽ���帺���ײ�״̬Ϊ�ײ��ڣ��������һ���ײ������ݣ�
			//ʵ�ս�����������¼��㣨���ݵ�һ��ݺ͵ڶ����ȷ�Ͻ��Ʒ�ʱ��Ϊ��ǰʱ�䣩
			result=onSaveExeIbsOrdd(parmIbsOrdd,caseNo,noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
					j, admParm, numberParm, connection,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: getExeUnBill
    * @Description: TODO(�����ײ�ҽ���˷Ѳ����߼�)
    * @author pangben
    * @param orderSum �ײ�����
    * @param execDosageQty �Ѿ������ҽ������
    * @return
    * @throws
     */
    private TParm getExeUnBill(double orderSum,double execDosageQty,double diffQty,TParm noExeBillParm,int j
    		,TParm parmIbsOrdd,String caseNo,TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
    	//�Ƚ�ͳ�Ƶ��������ײ���ϸ�л��ܳ���ҽ������
    	if (execDosageQty>0) {//�����ǰ�Ѿ��������ҽ����������0������ִ��ת�������
    		diffQty=execDosageQty+noExeBillParm.getDouble("DOSAGE_QTY",j);//�Ѿ�ִ�е���������Ҫ�����������Ƚ�
    		if (diffQty>=0) {//�Ѿ�ִ�е��ײ���ҽ������������Ҫ������ҽ������
    			//���˷�ҽ���ĳ�����
    			result=onSaveUnExeIbsOrdd(parmIbsOrdd,caseNo,diffQty, noExeBillParm.getDouble("DOSAGE_QTY",j), noExeBillParm.getValue("ORDER_CODE",j),
						j, admParm, numberParm, connection,statCode,caseNoNew);
				if (result.getErrCode()<0) {
					return result;
				}
			}else{//������ת����������������������ֿ�
				//���˼Ʒ�ҽ�������ֿ�
				//�ײ�������
				result=onSaveUnExeIbsOrddOne(parmIbsOrdd,caseNo, noExeBillParm.getValue("ORDER_CODE",j),-execDosageQty,
						noExeBillParm.getDouble("DOSAGE_QTY",j), admParm, numberParm, connection,statCode,caseNoNew);
				if (result.getErrCode()<0) {
					return result;
				}
			}
    	}else{
    		return null;
    	}
    	return result;
    }
    private TParm onSaveUnExeIbsOrddOne(TParm parmIbsOrdd,String caseNo,String orderCode,double diffQty,
    		double noExeDosageQty,TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//����ҽ������
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveUnOrderSetOrddParmOne(admParm, parmIbsOrddNew, numberParm, connection, diffQty,1, noExeDosageQty*-1,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveUnOrddParmOne(admParm, parmIbsOrdd, numberParm, diffQty,1, noExeDosageQty*-1, connection,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: getOrderSetParm
    * @Description: TODO(��ѯ����ҽ��ϸ��)
    * @author pangben
    * @param selectparm
    * @return
    * @throws
     */
    private TParm getOrderSetParm(TParm selectparm){
    	//TParm orderParm=null;
    	String orderSql="SELECT A.*,B.IPD_NO AS IPD_NO_M, B.MR_NO AS MR_NO_M, B.DEPT_CODE AS DEPT_CODE_M,"+ 
        "B.STATION_CODE AS STATION_CODE_M, B.BED_NO AS BED_NO_M, B.DATA_TYPE AS DATA_TYPE_M, "+
        "B.OPT_USER AS OPT_USER_M, B.OPT_DATE AS OPT_DATE_M,C.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,C.CHARGE_HOSP_CODE, "+
        "B.REGION_CODE AS REGION_CODE_M, B.COST_CENTER_CODE AS COST_CENTER_CODE_M,A.DOSAGE_QTY/"+selectparm.getDouble("DOSAGE_QTY")
        +" DOSAGE_QTY_NEW FROM IBS_ORDD A,IBS_ORDM B,SYS_FEE C " +
        "WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ AND A.ORDER_CODE=C.ORDER_CODE AND A.CASE_NO='"+selectparm.getData("CASE_NO")+"' " +
        "AND A.CASE_NO_SEQ='"+selectparm.getValue("CASE_NO_SEQ")+"' AND A.ORDERSET_CODE='"+selectparm.getValue("ORDERSET_CODE")+"'" +
		  " AND A.ORDERSET_GROUP_NO='"+selectparm.getValue("ORDERSET_GROUP_NO")+
      "' AND (A.INCLUDE_EXEC_FLG='N' OR A.INCLUDE_EXEC_FLG IS NULL)";
    	//System.out.println("orderSql:::"+orderSql);
		TParm result=new TParm(TJDODBTool.getInstance().select(orderSql));
    	return result;
    }
    /**
     * 
    * @Title: onSaveExeIbsOrdd
    * @Description: TODO(��Ҫ���������ݣ��ײ�������)
    * @author pangben
    * @param caseNo
    * @param noExeBillParm
    * @param j
    * @param admParm
    * @param maxCaseNoSeqParm
    * @param maxCaseNoSeq
    * @param numberParm
    * @param connection
    * @return
    * @throws
     */
    private TParm onSaveExeIbsOrdd(TParm parmIbsOrdd,String caseNo,double dosageQty,String orderCode,int j,
    		TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//����ҽ������
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveOrderSetOrddParm(admParm, parmIbsOrddNew, caseNo,
					connection, true, numberParm, dosageQty,statCode,parmIbsOrdd,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveOrddParm(admParm, parmIbsOrdd, caseNo, 
					connection, true,numberParm, dosageQty,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: onSaveExeIbsOrdd
    * @Description: TODO(��Ҫ���������ݣ��ײ�������)�˷�
    * @author pangben
    * @param caseNo
    * @param noExeBillParm
    * @param j
    * @param admParm
    * @param maxCaseNoSeqParm
    * @param maxCaseNoSeq
    * @param numberParm
    * @param connection
    * @return
    * @throws
     */
    private TParm onSaveUnExeIbsOrdd(TParm parmIbsOrdd,String caseNo,double diffQty,double dosageQty,String orderCode,int j,
    		TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//����ҽ������
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveOrderSetOrddParm(admParm, parmIbsOrddNew, caseNo,
					connection, true, numberParm, dosageQty,statCode,parmIbsOrdd,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveUnOrddParm(admParm, parmIbsOrdd, caseNo, 
					connection,numberParm,diffQty, dosageQty,statCode);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: onSaveExeIbsOrddOne
    * @Description: TODO(�����ײ��ں��ײ�������)
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private TParm onSaveExeIbsOrddOne(TParm parmIbsOrdd,String caseNo,String orderCode,double diffQty,
    		double noExeDosageQty,TParm admParm,TParm numberParm,TConnection connection,String statCode,String caseNoNew){
    	TParm result=new TParm();
		if (parmIbsOrdd.getValue("ORDER_CODE",0).equals(parmIbsOrdd.getValue("ORDERSET_CODE",0))
				&&parmIbsOrdd.getValue("INDV_FLG",0).equals("N")) {//����ҽ������
			TParm parmIbsOrddNew=getOrderSetParm(parmIbsOrdd.getRow(0));
			result=onSaveOrderSetOrddParmOne(admParm, parmIbsOrddNew, numberParm, connection, diffQty,-1, noExeDosageQty,statCode,parmIbsOrdd,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}else{
			result=onSaveOrddParmOne(admParm, parmIbsOrdd, numberParm, diffQty,-1, noExeDosageQty, connection,statCode,caseNoNew);
			if (result.getErrCode()<0) {
				result.setErr(-1,"����ʧ��:"+result.getErrText());
				return result;
			}
		}
		return result;
    }
    /**
     * 
    * @Title: getOnExeIncludeExecFlgBill
    * @Description: TODO(��ѯ�Ѿ��������ҽ��,ͳ������)�շ�
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private double getOnExeIncludeExecFlgBill(String caseNo,String orderCode){
    	String sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY  " +
    			"FROM IBS_ORDD WHERE CASE_NO IN ("+caseNo+") AND ORDER_CODE='"+orderCode+"' AND  INCLUDE_EXEC_FLG='Y'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount("DOSAGE_QTY")<=0||result.getDouble("DOSAGE_QTY",0)<=0) {//û�д����ҽ��
			return 0.00;
		}
    	return result.getDouble("DOSAGE_QTY",0);
    }
    /**
     * 
    * @Title: getOnExeUnIncludeExecFlgBill
    * @Description: TODO(��ѯ�Ѿ������������ҽ��,ͳ������)�˷�
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private double getOnExeUnIncludeExecFlgBill(String caseNo,String orderCode){
    	String sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY  " +
		"FROM IBS_ORDD WHERE CASE_NO IN ("+caseNo+") AND ORDER_CODE='"+orderCode+"' AND INCLUDE_FLG='Y' AND INCLUDE_EXEC_FLG='Y'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getCount("DOSAGE_QTY")<=0||result.getDouble("DOSAGE_QTY",0)<=0) {//û�д����ҽ��
    		return 0.00;
    	}
    	return result.getDouble("DOSAGE_QTY",0);
    }
    /**
     * 
    * @Title: getIbsOrddOrderParm
    * @Description: TODO(�帺ʹ������)
    * @author pangben
    * @param caseNo
    * @param orderCode
    * @return
    * @throws
     */
    private TParm getIbsOrddOrderParm(String caseNo,String orderCode){
    	String sql="SELECT A.*,B.IPD_NO AS IPD_NO_M, B.MR_NO AS MR_NO_M, B.DEPT_CODE AS DEPT_CODE_M,"+ 
                   "B.STATION_CODE AS STATION_CODE_M, B.BED_NO AS BED_NO_M, B.DATA_TYPE AS DATA_TYPE_M, "+
                   "B.OPT_USER AS OPT_USER_M, B.OPT_DATE AS OPT_DATE_M, "+
                   "B.REGION_CODE AS REGION_CODE_M, B.COST_CENTER_CODE AS COST_CENTER_CODE_M," +
                   "C.CAT1_TYPE AS SYS_FEE_CAT1_TYPE,C.CHARGE_HOSP_CODE "+
                   " FROM IBS_ORDD A ,IBS_ORDM B,SYS_FEE C WHERE A.CASE_NO=B.CASE_NO AND A.CASE_NO_SEQ=B.CASE_NO_SEQ " +
                   " AND A.ORDER_CODE=C.ORDER_CODE AND " +
		"A.CASE_NO='"+caseNo+"' AND (A.INCLUDE_EXEC_FLG='N' OR A.INCLUDE_EXEC_FLG IS NULL) AND" +
				" (A.ORDERSET_CODE IS NULL OR A.ORDERSET_CODE=A.ORDER_CODE) AND A.ORDER_CODE='"+orderCode+"'";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getAdmInpInfo
    * @Description: TODO(��ѯ����δ����ҽ������Ժ�ײͲ���)
    * @author pangben
    * @return
    * @throws
     */
    public TParm getAdmInpInfo(){
    	String sql="SELECT A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG FROM ADM_INP A,IBS_ORDD B WHERE A.CASE_NO=B.CASE_NO" +
    			" AND A.LUMPWORK_CODE IS NOT NULL AND A.DS_DATE IS NULL AND B.INCLUDE_EXEC_FLG='N' AND A.CANCEL_FLG ='N' " +
    			"GROUP BY A.CASE_NO,A.LUMPWORK_CODE,A.MR_NO,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.M_CASE_NO,A.NEW_BORN_FLG";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getNoIncludeExecFlgBill
    * @Description: TODO(���ݾ���Ų�ѯδ����ļƷ�ҽ������,����ҽ�����룬��������)
    * @author pangben
    * @param caseNo
    * @return
    * @throws
     */
    public TParm getNoIncludeExecFlgBill(String caseNo){
    	String sql="SELECT SUM(DOSAGE_QTY) DOSAGE_QTY,ORDER_CODE FROM IBS_ORDD WHERE " +
    			"CASE_NO='"+caseNo+"' AND (INCLUDE_EXEC_FLG='N' OR INCLUDE_EXEC_FLG IS NULL) AND" +
    					" (ORDERSET_CODE IS NULL OR ORDERSET_CODE=ORDER_CODE) GROUP BY ORDER_CODE ORDER BY ORDER_CODE, DOSAGE_QTY";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
     * 
    * @Title: getMemPatPackAgeSection
    * @Description: TODO(������ѯ������δִ�мƷ�ҽ��������ҽ������������ѯ��ǰ�ײ͵�ҽ����ϸ����,����ҽ������)
    * @author pangben
    * @param packCode �ײʹ���
    * @param orderCode ҽ������
    * @return
    * @throws
     */
    public TParm getMemPatPackAgeSection(String caseNo,String packCode,String orderCode){
    	String sql="SELECT SUM(ORDER_NUM) ORDER_NUM, ORDER_CODE,UN_NUM_FLG FROM MEM_PAT_PACKAGE_SECTION_D " +
    			"WHERE CASE_NO='"+caseNo+"' AND PACKAGE_CODE='"+packCode+"' AND ORDER_CODE='"+orderCode+"' GROUP BY ORDER_CODE,UN_NUM_FLG";
    	TParm result=new TParm(TJDODBTool.getInstance().select(sql));
    	return result;
    }
    /**
	 * ��ѯ����������
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 * caowl
	 */
	public TParm selMaxCaseNoSeq(String caseNo) {
		String sql = " SELECT MAX(CASE_NO_SEQ) AS CASE_NO_SEQ FROM IBS_ORDM WHERE CASE_NO = '"
				+ caseNo + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
	private TParm onSaveIbsOrdm(TParm parmIbsOrdm,TParm numberParm,String caseNo,TConnection connection){
		TParm insertIbsOrdMNegativeParm = new TParm();					
		insertIbsOrdMNegativeParm.setData("CASE_NO",caseNo);
		insertIbsOrdMNegativeParm.setData("CASE_NO_SEQ",numberParm.getInt("CASE_NO_SEQ"));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		insertIbsOrdMNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrdMNegativeParm.setData("IPD_NO",parmIbsOrdm.getData("IPD_NO_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("IPD_NO_M"));
		insertIbsOrdMNegativeParm.setData("MR_NO",parmIbsOrdm.getData("MR_NO_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("MR_NO_M"));
		insertIbsOrdMNegativeParm.setData("DEPT_CODE",parmIbsOrdm.getData("DEPT_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("DEPT_CODE_M"));
		insertIbsOrdMNegativeParm.setData("STATION_CODE",parmIbsOrdm.getData("STATION_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("STATION_CODE_M"));
		insertIbsOrdMNegativeParm.setData("BED_NO",parmIbsOrdm.getData("BED_NO_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("BED_NO_M"));
		insertIbsOrdMNegativeParm.setData("DATA_TYPE","1");
		insertIbsOrdMNegativeParm.setData("BILL_NO","");
		insertIbsOrdMNegativeParm.setData("OPT_USER","PACK_BATCH");
		insertIbsOrdMNegativeParm.setData("OPT_TERM","127.0.0.1");
		insertIbsOrdMNegativeParm.setData("REGION_CODE",parmIbsOrdm.getData("REGION_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("REGION_CODE_M"));
		insertIbsOrdMNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdm.getData("COST_CENTER_CODE_M") == null ? new TNull(String.class) : parmIbsOrdm.getData("COST_CENTER_CODE_M"));
		
		TParm result =IBSOrdmTool.getInstance().insertdataM(insertIbsOrdMNegativeParm,connection);
        if (result.getErrCode() < 0) {
            System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
	}
	private TParm getCommOnExeIbsOrdd(TParm parmIbsOrdd,int m,int seqNo,int maxCaseNoSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,int mas,String orderNo,boolean flg,String statCode,double lumpworkRate,
			String lumpWorkCode,String level,int orderSeq,String caseNoNew,boolean billExeFlg){
		TParm insertIbsOrddNegativeParm = new TParm();		
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, m, sysDate,mas,dosageQty);
		double ownRate =0.00;
		double ownPrice=0.00;
		double [] sumPrice = new double[2];
		if (flg) {//������
			sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",m),level);
			ownPrice=sumPrice[0];
			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",m) ,
					parmIbsOrdd.getValue("ORDER_CODE",m));//������������ȷ��
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("OWN_AMT",dosageQty*
					ownPrice*mas);
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
		}else{
			//ҩƷ��Ѫ�Ѹ������ͳ��
			if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m)
					&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
					null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
						parmIbsOrdd.getValue("ORDER_CODE", m));
				sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE", m),level);
				ownPrice=sumPrice[0];
			}else{
				ownRate=lumpworkRate;//������ ����סԺ�Ǽ����õ��ۿ�ͳ��
				ownPrice = IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpWorkCode,
						parmIbsOrdd.getValue("ORDER_CODE", m), level);
			}
			
    		insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
    		insertIbsOrddNegativeParm.setData("OWN_AMT",dosageQty*ownPrice*mas);
//			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",m) ,
//					parmIbsOrdd.getValue("ORDER_CODE",m));//������ ����סԺ�ǼǼ�����ۿ�ȷ��
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		}
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1);
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",orderSeq);
		TParm result =null;
		if (billExeFlg) {
			insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
			result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
		}else{
			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		}

        return result;
	}
	/**
	 * ����ҽ�������ײ�
	 * @param parmIbsOrdd
	 * @param m
	 * @param seqNo
	 * @param maxCaseNoSeq
	 * @param orderSeq
	 * @param dosageQty
	 * @param sysDate
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param bil
	 * @param groupNo
	 * @param connection
	 * @param mas
	 * @param orderNo
	 * @param flg
	 * @param statCode
	 * @param lumpworkRate
	 * @param lumpWorkCode
	 * @param level
	 * @param parmIbsOrddOld
	 * @param caseNo
	 * @return
	 */
	private TParm getCommOnExeOrderSetIbsOrdd(int seqNo,int maxCaseNoSeq,int orderSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,String orderNo,boolean flg,String statCode,double lumpworkRate,
			String lumpWorkCode,String level,TParm parmIbsOrddOld,String caseNo,String caseNoNew){
		TParm result=new TParm();
		if(flg){//����
			result=lumpWorkOutOnExeOrderSet(seqNo, maxCaseNoSeq, dosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil,
					groupNo, connection,orderNo, statCode, lumpworkRate, level, parmIbsOrddOld, caseNo, orderSeq, true);
		}else{
			result=lumpWorkInOnExeOrderSet(seqNo, maxCaseNoSeq, dosageQty, sysDate, CTZ1,
					CTZ2, CTZ3, bil, groupNo, connection, orderNo, statCode, lumpworkRate, 
					lumpWorkCode, level, parmIbsOrddOld, caseNo, orderSeq, false,caseNoNew);
		}
		return result;
	}
	/**
	 * ���ڼ���ҽ��ִ��
	 * @param parmIbsOrdd
	 * @param seqNo
	 * @param maxCaseNoSeq
	 * @param dosageQty
	 * @param sysDate
	 * @param CTZ1
	 * @param CTZ2
	 * @param CTZ3
	 * @param bil
	 * @param groupNo
	 * @param connection
	 * @param mas
	 * @param orderNo
	 * @param statCode
	 * @param lumpworkRate
	 * @param level
	 * @param parmIbsOrddOld
	 * @param caseNo
	 * @param orderSeq
	 * @param flg
	 * @return
	 */
	private TParm lumpWorkInOnExeOrderSet(int seqNo,int maxCaseNoSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,String orderNo,String statCode,double lumpworkRate,String lumpworkCode,
			String level,TParm parmIbsOrddOld,String caseNo,int orderSeq,boolean flg,String caseNoNew){
		
		//���»���ۿۺ�Ľ��
    	String querySql = "SELECT A.ORDER_CODE,B.ORDER_CAT1_CODE,B.CAT1_TYPE,A.ORDERSET_CODE,B.CHARGE_HOSP_CODE HEXP_CODE," +
    			"A.ORDER_NUM MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,A.ORDER_NUM DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," +
    			"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.UNIT_PRICE AS OWN_PRICE " +
    			"FROM MEM_PAT_PACKAGE_SECTION_D A ,SYS_FEE B,SYS_CHARGE_HOSP C " +
    			"WHERE A.ORDER_CODE=B.ORDER_CODE AND B.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE  AND A.CASE_NO='" +caseNo
		+ "' AND A.PACKAGE_CODE = '"+lumpworkCode+"' AND A.ORDERSET_CODE='"+parmIbsOrddOld.getValue("ORDER_CODE",0)+"'";
    	TParm parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
    	TParm insertIbsOrddNegativeParm=null;
    	TParm result=new TParm();
    	if(parmIbsOrddNew.getCount()>0){//���ڼ���ҽ��
    		for (int m = 0; m < parmIbsOrddNew.getCount(); m++) {
    			//���»���ۿۺ�Ľ��
		        insertIbsOrddNegativeParm = new TParm();		
				insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);							
				insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
				insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
				if (dosageQty<0) {
					getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,
							-1,dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m))*-1,parmIbsOrddOld);
				}else{
					getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,1,
							dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m)),parmIbsOrddOld);
				}
				insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
				insertIbsOrddNegativeParm.setData("ORDER_SEQ",orderSeq);
				++orderSeq;
				insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
				double ownRate =0.00;
				double ownPrice=0.00;
				//ҩƷ��Ѫ�Ѹ������ͳ��
				if(null!=parmIbsOrddNew.getValue("CAT1_TYPE",m)
						&&parmIbsOrddNew.getValue("CAT1_TYPE",m).equals("PHA")||
						null!=parmIbsOrddNew.getValue("HEXP_CODE",m)&&parmIbsOrddNew.getValue("HEXP_CODE",m).equals("RA")){
					 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE",m) ,
							 parmIbsOrddNew.getValue("ORDER_CODE", m));		 
				}else{
					ownRate =lumpworkRate;//�����ڸ���סԺ�Ǽǲ���ʱ������ۿ�ͳ��
				}
				ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", m);
				if (parmIbsOrddNew.getValue("ORDER_CODE", m).equals(parmIbsOrddNew.getValue("ORDERSET_CODE", m))) {
					insertIbsOrddNegativeParm.setData("OWN_PRICE",0);
					insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
					insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
					insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
					insertIbsOrddNegativeParm.setData("OWN_AMT",0);
					insertIbsOrddNegativeParm.setData("TOT_AMT",0);
				}else{
					insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
					insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
					insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
					insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
					insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*
							insertIbsOrddNegativeParm.getDouble("DOSAGE_QTY"));
					insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
				}

				++seqNo;
				result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		        if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
	                return result;
		        }
    		}
    		result.setData("SEQ_NO",seqNo);
    		result.setData("ORDER_SEQ",orderSeq);
    	}else{
    		result =lumpWorkOutOnExeOrderSet(seqNo, maxCaseNoSeq, dosageQty, sysDate, CTZ1, CTZ2,
    				CTZ3, bil, groupNo, connection, orderNo, statCode, lumpworkRate, level,
    				parmIbsOrddOld, caseNo, orderSeq, flg);
    		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	        }
    	}
    	return result;
	}
	/**
	 * �ײ��⼯��ҽ��ִ��
	 * 
	 * @return
	 */
	private TParm lumpWorkOutOnExeOrderSet(int seqNo,int maxCaseNoSeq,
			double dosageQty,Timestamp sysDate ,String CTZ1,String CTZ2,String CTZ3,BIL bil,
			int groupNo,TConnection connection,String orderNo,String statCode,double lumpworkRate,
			String level,TParm parmIbsOrddOld,String caseNo,int orderSeq,boolean flg){

		//�ײ��ڲ����ڴ˼���ҽ��,��ѯSYS_FEEת������
		String querySql = "SELECT A.ORDER_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,A.ORDER_CODE ORDERSET_CODE,A.CHARGE_HOSP_CODE HEXP_CODE,"
						+"1 MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE, 1 DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT,"
						+"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.OWN_PRICE,A.OWN_PRICE2,A.OWN_PRICE3  FROM SYS_FEE A,SYS_CHARGE_HOSP C  "
						+"WHERE A.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE AND A.ORDER_CODE='"+ parmIbsOrddOld.getValue("ORDER_CODE",0)+"' " 
						+"UNION ALL  SELECT A.ORDER_CODE,A.ORDER_CAT1_CODE,A.CAT1_TYPE,B.ORDERSET_CODE,A.CHARGE_HOSP_CODE HEXP_CODE," 
						+"B.DOSAGE_QTY MEDI_QTY,A.UNIT_CODE AS MEDI_UNIT,'' DOSE_CODE,B.DOSAGE_QTY,A.UNIT_CODE DOSAGE_UNIT," 
						+"C.IPD_CHARGE_CODE REXP_CODE,A.ORDER_DESC ORDER_CHN_DESC,A.OWN_PRICE,A.OWN_PRICE2,A.OWN_PRICE3 FROM SYS_FEE A,SYS_ORDERSETDETAIL B,SYS_CHARGE_HOSP C " 
						+" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.CHARGE_HOSP_CODE=C.CHARGE_HOSP_CODE AND B.ORDERSET_CODE='"
						+ parmIbsOrddOld.getValue("ORDER_CODE",0)+"'";
		TParm parmIbsOrddNew = new TParm(TJDODBTool.getInstance().select(querySql));
		TParm insertIbsOrddNegativeParm=null;
		TParm result=new TParm();
		for (int m = 0; m < parmIbsOrddNew.getCount(); m++) {
			//���»���ۿۺ�Ľ��
	        insertIbsOrddNegativeParm = new TParm();		
			insertIbsOrddNegativeParm.setData("CASE_NO",caseNo);					
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);							
			insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
			insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
			if (dosageQty<0) {
				getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,
						-1,dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m))*-1,parmIbsOrddOld);
			}else{
				getIbsOrddParmOne(insertIbsOrddNegativeParm, parmIbsOrddNew, m, sysDate,1,
						dosageQty*Math.abs(parmIbsOrddNew.getDouble("DOSAGE_QTY",m)),parmIbsOrddOld);
			}
			insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
			insertIbsOrddNegativeParm.setData("ORDER_SEQ",orderSeq);
			++orderSeq;
			insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
			double ownRate =0.00;
			double ownPrice=0.00;
			if(flg){//����
				 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE",m) ,
						 parmIbsOrddNew.getValue("ORDER_CODE", m));		 
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
			}else{
				//ҩƷ��Ѫ�Ѹ������ͳ��
				if(null!=parmIbsOrddNew.getValue("CAT1_TYPE",m)
						&&parmIbsOrddNew.getValue("CAT1_TYPE",m).equals("PHA")||
						null!=parmIbsOrddNew.getValue("HEXP_CODE",m)&&parmIbsOrddNew.getValue("HEXP_CODE",m).equals("RA")){
					 ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrddNew.getValue("HEXP_CODE",m) ,
							 parmIbsOrddNew.getValue("ORDER_CODE", m));		 
				}else{
					ownRate =lumpworkRate;//�����ڸ���סԺ�Ǽǲ���ʱ������ۿ�ͳ��
				}
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
			}
			if ("2".equals(level)) {
                ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE2", m);
            } else if ("3".equals(level)) {
                ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE3", m);
            } else
                ownPrice = parmIbsOrddNew.getDouble("OWN_PRICE", m);
			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			insertIbsOrddNegativeParm.setData("NHI_PRICE",0);
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*
					insertIbsOrddNegativeParm.getDouble("DOSAGE_QTY"));
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
			++seqNo;
			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
	        if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	        }
		}	
		result.setData("SEQ_NO",seqNo);
		result.setData("ORDER_SEQ",orderSeq);
		return result;
	}
	/**
	 * ����ҽ���������ڲ����������
	 * @param parm
	 * @param parmIbsOrdd
	 * @param numberParm
	 * @param connection
	 * @param diffQty �ײ�������
	 * @param mas
	 * @param noExeDosageQty �ײ�������
	 * @param statCode
	 * @param parmIbsOrddOld
	 * @return
	 */
	private TParm onSaveOrderSetOrddParmOne(TParm parm,TParm parmIbsOrdd,TParm numberParm,
			TConnection connection,double diffQty,int mas, double noExeDosageQty,String statCode,
			TParm parmIbsOrddOld,String caseNoNew){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		String level=parm.getValue("SERVICE_LEVEL");//����ȼ�
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//�ײʹ���
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int groupNo=numberParm.getInt("GROUP_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        //TParm insertIbsOrddNegativeParm =null;
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		int orderSeq=1;
        if(parmIbsOrdd.getCount("CASE_NO")>0){
			for(int m = 0;m<parmIbsOrdd.getCount("CASE_NO");m++){
				//step7:������ϸ�����븺����
				//ibs_ordd���븺����  case_no_seq_new1,bill_date-->sysdate,���Ϊ��
				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
						noExeDosageQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, 
						CTZ2, CTZ3, bil, groupNo, connection,mas,orderNo,false,statCode,lumpworkRate,lumpworkCode,
						level,orderSeq,caseNoNew,true);
				if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
		            return result;
			    }
				++seqNo;
				++orderSeq;
//				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
//						diffQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3,
//						bil, groupNo+1, connection,-mas,orderNo,false,statCode,lumpworkRate,lumpworkCode,level);
//				if (result.getErrCode() < 0) {
//		        	System.out.println(result.getErrName() + " " + result.getErrText());
//		            return result;
//			    }
//				++seqNo;
//				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
//						(noExeDosageQty-diffQty)*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3,
//						bil, groupNo+2, connection,-mas,orderNo,true,statCode,lumpworkRate,lumpworkCode,level);
//				if (result.getErrCode() < 0) {
//		        	System.out.println(result.getErrName() + " " + result.getErrText());
//		            return result;
//			    }
//				++seqNo;
			}
			//ִ��״̬�޸�  BILL_EXE_FLG
			result = getExeBillExeFlg(parmIbsOrddOld.getValue("CASE_NO",0),true, parmIbsOrddOld.getValue("ORDER_CODE",0), connection);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
			}
			//�����ݲ�������ҽ��
			//���������
			//��������ҽ�� ���¼������ 1. �ײ���ת���ײ��⣬��ѯSYS_FEE����ҽ��ϸ���Լ����ã�����SYS_FEE���������IBS_ORDD��
	        //2.�ײ���ת���ײ��ڣ���ѯMEM_PAT_PACKAGE_SECTION_D��˼���ҽ��ϸ���Լ����ã�
	        //����MEM_PAT_PACKAGE_SECTION_D���������IBS_ORDD��
	        //���MEM_PAT_PACKAGE_SECTION_D �����ڴ˼���ҽ������,�����SYS_FEE���������IBS_ORDD��
			result=getCommOnExeOrderSetIbsOrdd(seqNo, maxCaseNoSeq, orderSeq, diffQty,
					sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo+1, connection, orderNo, false, statCode, 
					lumpworkRate, lumpworkCode, level, parmIbsOrddOld, parmIbsOrddOld.getValue("CASE_NO",0),caseNoNew);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
			seqNo=result.getInt("SEQ_NO");
			orderSeq=result.getInt("ORDER_SEQ");
			result=getCommOnExeOrderSetIbsOrdd(seqNo, maxCaseNoSeq, orderSeq, (noExeDosageQty-diffQty),
					sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo+2, connection, orderNo, true, statCode, 
					lumpworkRate, lumpworkCode, level, parmIbsOrddOld, parmIbsOrddOld.getValue("CASE_NO",0),caseNoNew);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
		}
        return result;
	}
	/**
	 * 
	* @Title: onSaveUnOrderSetOrddParmOne
	* @Description: TODO(�˷Ѳ���)
	* @author pangben
	* @param parm
	* @param parmIbsOrdd
	* @param numberParm
	* @param connection
	* @param diffQty
	* @param mas
	* @param noExeDosageQty
	* @param statCode
	* @return
	* @throws
	 */
	private TParm onSaveUnOrderSetOrddParmOne(TParm parm,TParm parmIbsOrdd,TParm numberParm,
			TConnection connection,double diffQty,int mas, double noExeDosageQty,String statCode,String caseNoNew){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		String level=parm.getValue("SERVICE_LEVEL");//����ȼ�
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//�ײʹ���
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int groupNo=numberParm.getInt("GROUP_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        int orderSeq=1;
        //TParm insertIbsOrddNegativeParm =null;
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
        if(parmIbsOrdd.getCount("CASE_NO")>0){
			for(int m = 0;m<parmIbsOrdd.getCount("CASE_NO");m++){
				//step7:������ϸ�����븺����
				//ibs_ordd���븺����  case_no_seq_new1,bill_date-->sysdate,���Ϊ��
//				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
//						noExeDosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo, connection,mas,orderNo,false,statCode);
//				if (result.getErrCode() < 0) {
//		        	System.out.println(result.getErrName() + " " + result.getErrText());
//		            return result;
//			    }
//				++seqNo;
				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
						diffQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3, 
						bil, groupNo+1, connection,mas,orderNo,true,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,true);
				if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
		            return result;
			    }
				++seqNo;
				++orderSeq;
				result=getCommOnExeIbsOrdd(parmIbsOrdd, m, seqNo, maxCaseNoSeq,
						(diffQty+noExeDosageQty)*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)), sysDate, CTZ1, CTZ2, CTZ3, 
						bil, groupNo+2, connection,1,orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
				if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
		            return result;
			    }
				++seqNo;
				++orderSeq;
			}
		}
        return result;
	}
	/**
	 * 
	* @Title: onSaveOrderSetOrddParm
	* @Description: TODO(����ҽ�������ײ���)
	* @author pangben
	* @param parm
	* @param maxCaseNoSeqParm
	* @param parmIbsOrdd
	* @param caseNo
	* @param connection
	* @param flg
	* @param maxCaseNoSeq
	* @param numberParm
	* @param dosageQty
	* @return
	* @throws
	 */
	private TParm onSaveOrderSetOrddParm(TParm parm ,TParm parmIbsOrdd,
			String caseNo,TConnection connection,boolean flg,TParm numberParm,
			double dosageQty,String statCode,TParm parmIbsOrddOld,String caseNoNew){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		String level=parm.getValue("SERVICE_LEVEL");//����ȼ�
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//�ײʹ���
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();

		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int groupNo=numberParm.getInt("GROUP_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
		int orderSeq=1;
        BIL bil = new BIL();
        TParm result=new TParm();
        TParm insertIbsOrddNegativeParm =null;
		if(parmIbsOrdd.getCount("CASE_NO")>0){
			String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
			for(int m = 0;m<parmIbsOrdd.getCount("CASE_NO");m++){
				//step7:������ϸ�����븺����
				//ibs_ordd���븺����  case_no_seq_new1,bill_date-->sysdate,���Ϊ��
				insertIbsOrddNegativeParm = new TParm();		
				insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", m));
				insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
				insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
				if (dosageQty<0) {//����ҽ���������¼���
					getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, m, sysDate,1,dosageQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m))*-1);
				}else{
					getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, m, sysDate,-1,dosageQty*Math.abs(parmIbsOrdd.getDouble("DOSAGE_QTY_NEW",m)));
				}
				double ownRate =0.00;
				//ҩƷ��Ѫ�Ѹ������ͳ��
				if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m)
						&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",m).equals("PHA")||
						null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",m).equals("RA")){
					ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
							parmIbsOrdd.getValue("ORDER_CODE", m));
				}else{
					ownRate=lumpworkRate;//������ ����סԺ�Ǽ����õ��ۿ�ͳ��
				}
				
//				double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", m) ,
//						parmIbsOrdd.getValue("ORDER_CODE", m));//������ ��һ��ݺ͵ڶ����ͳ��
				insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
				insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
				insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate, 2));
				insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",groupNo);
				insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
				insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
				++seqNo;
				result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
		        if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
	                return result;
		        }
		       
			}
			//ִ��״̬�޸�  BILL_EXE_FLG
			result = getExeBillExeFlg(caseNo,true, parmIbsOrddOld.getValue("ORDER_CODE",0), connection);
			if (result.getErrCode() < 0) {
		        	System.out.println(result.getErrName() + " " + result.getErrText());
	                return result;
		    }
			//���������
			//��������ҽ�� ���¼������ 1. �ײ���ת���ײ��⣬��ѯSYS_FEE����ҽ��ϸ���Լ����ã�����SYS_FEE���������IBS_ORDD��
	        //2.�ײ���ת���ײ��ڣ���ѯMEM_PAT_PACKAGE_SECTION_D��˼���ҽ��ϸ���Լ����ã�
	        //����MEM_PAT_PACKAGE_SECTION_D���������IBS_ORDD��
	        //���MEM_PAT_PACKAGE_SECTION_D �����ڴ˼���ҽ������,�����SYS_FEE���������IBS_ORDD��
			//�����ݲ�������ҽ��
			//���������
			//��������ҽ�� ���¼������ 1. �ײ���ת���ײ��⣬��ѯSYS_FEE����ҽ��ϸ���Լ����ã�����SYS_FEE���������IBS_ORDD��
	        //2.�ײ���ת���ײ��ڣ���ѯMEM_PAT_PACKAGE_SECTION_D��˼���ҽ��ϸ���Լ����ã�
	        //����MEM_PAT_PACKAGE_SECTION_D���������IBS_ORDD��
	        //���MEM_PAT_PACKAGE_SECTION_D �����ڴ˼���ҽ������,�����SYS_FEE���������IBS_ORDD��
			result=getCommOnExeOrderSetIbsOrdd(seqNo, maxCaseNoSeq, orderSeq, dosageQty,
					sysDate, CTZ1, CTZ2, CTZ3, bil, groupNo+1, connection, orderNo, flg, statCode, 
					lumpworkRate, lumpworkCode, level, parmIbsOrddOld, parmIbsOrddOld.getValue("CASE_NO",0),caseNoNew);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
			numberParm.setData("GROUP_NO",groupNo+2);//ȷ��GROUP_NOΨһ��	
		}
		return result;
	}
	/**
	 * 
	* @Title: onSaveUnOrddParmOne
	* @Description: TODO(ҩƷ��ҩ����)
	* @author pangben
	* @param parm
	* @param parmIbsOrdd
	* @param numberParm
	* @param diffQty
	* @param mas
	* @param noExeDosageQty
	* @param connection
	* @param statCode
	* @return
	* @throws
	 */
	private TParm onSaveUnOrddParmOne(TParm parm,TParm parmIbsOrdd,
			TParm numberParm,double diffQty,int mas,double noExeDosageQty,TConnection connection,String statCode,String caseNoNew){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		String level=parm.getValue("SERVICE_LEVEL");//����ȼ�
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//�ײʹ���
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        int orderSeq=1;
        //TParm insertIbsOrddNegativeParm = new TParm();
        result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				noExeDosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0, 
				connection,mas,orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,true);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		result = getExeBillExeFlg(parmIbsOrdd.getValue("CASE_NO",0),false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		//diffQty�ײ����Ѿ�ִ�е�ҽ��    ��diffQty�ײ���
		if (diffQty!=0) {
			++seqNo;
			++orderSeq;
			result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
					diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil,0, connection,1,orderNo,
					true,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
		}
		//diffQty+noExeDosageQty Ϊ����  ִ�е� �ײ���
		if (-noExeDosageQty-diffQty!=0) {
			++seqNo;
			++orderSeq;
			result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
					-noExeDosageQty-diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0,
					connection,1,orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
			if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
		    }
		}
		return result;
	}
	private TParm onSaveOrddParmOne(TParm parm,TParm parmIbsOrdd,
			TParm numberParm,double diffQty,int mas,double noExeDosageQty,TConnection connection,String statCode,String caseNoNew){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		String level=parm.getValue("SERVICE_LEVEL");//����ȼ�
		String lumpworkCode=parm.getValue("LUMPWORK_CODE");//�ײʹ���
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
		int orderSeq=1;
        BIL bil = new BIL();
        TParm result=new TParm();
        //TParm insertIbsOrddNegativeParm = new TParm();
        result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				noExeDosageQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0, connection,mas,
				orderNo,false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,true);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		result = getExeBillExeFlg(parmIbsOrdd.getValue("CASE_NO",0),false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	    }
		++seqNo;
		++orderSeq;
		result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil,0, connection,1,orderNo,
				false,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		++seqNo;
		++orderSeq;
		result=getCommOnExeIbsOrdd(parmIbsOrdd, 0, seqNo, maxCaseNoSeq,
				noExeDosageQty-diffQty, sysDate, CTZ1, CTZ2, CTZ3, bil, 0, 
				connection,1,orderNo,true,statCode,lumpworkRate,lumpworkCode,level,orderSeq,caseNoNew,false);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
	    }
		return result;
	}
	/**
	 * 
	* @Title: getIbsOrddParm
	* @Description: TODO(���ë@��IBS_ORDD���녢)
	* @author pangben
	* @param parm
	* @param parmIbsOrdm
	* @param parmIbsOrdd
	* @param caseNo
	* @param connection
	* @param flg
	* @return
	* @throws
	 */
	private TParm onSaveOrddParm(TParm parm,TParm parmIbsOrdd,
			String caseNo,TConnection connection,boolean flg,
			TParm numberParm,double dosageQty,String statCode,String caseNoNew){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		String lumpWorkCode=parm.getValue("LUMPWORK_CODE");
		String level=parm.getValue("SERVICE_LEVEL");//����ȼ�
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        TParm insertIbsOrddNegativeParm = new TParm();	
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null 
				? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));						
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,-1,dosageQty);
		double ownRate=0.00;
		//ҩƷ��Ѫ�Ѹ������ͳ��
		if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0)
				&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0).equals("PHA")||
				null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
					parmIbsOrdd.getValue("ORDER_CODE", 0));
		}else{
			ownRate=lumpworkRate;//������ ����סԺ�Ǽ����õ��ۿ�ͳ��
		}
//		double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
//				parmIbsOrdd.getValue("ORDER_CODE", 0));//������ ��һ��ݺ͵ڶ����ͳ��
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
		++seqNo;
		result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
        if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
        //ִ��״̬�޸�  BILL_EXE_FLG
		result = getExeBillExeFlg(caseNo,false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	    }
        //���»���ۿۺ�Ľ��
        insertIbsOrddNegativeParm = new TParm();		
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null ?
				new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));					
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);						
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		//getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		double ownPrice=0.00;
		TParm feeParm =null;
		double [] sumPrice=new double[2];
		if (flg) {//������
			sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",0),level);
			ownPrice=sumPrice[0];
			insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
					parmIbsOrdd.getValue("ORDER_CODE",0));//�������һ���
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*dosageQty);
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(ownPrice*dosageQty*ownRate,2));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
		}else{
			//ҩƷ��Ѫ�Ѹ������ͳ��
			if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0)
					&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0).equals("PHA")||
					null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
				ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
						parmIbsOrdd.getValue("ORDER_CODE", 0));
				sumPrice = IBSTool.getInstance().getOrderOwnPrice(parmIbsOrdd.getValue("ORDER_CODE",0),level);
				ownPrice=sumPrice[0];
			}else{
				ownRate=lumpworkRate;//������ ����סԺ�Ǽ����õ��ۿ�ͳ��
				ownPrice = IBSTool.getInstance().getLumpOrderOwnPrice(caseNoNew, lumpWorkCode, 
						parmIbsOrdd.getValue("ORDER_CODE", 0), level);
			}
			
            insertIbsOrddNegativeParm.setData("OWN_PRICE",ownPrice);
			insertIbsOrddNegativeParm.setData("OWN_AMT",ownPrice*dosageQty);
//			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
//					parmIbsOrdd.getValue("ORDER_CODE",0));//������ ��һ��ݺ͵ڶ����ͳ��
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(ownPrice*dosageQty*ownRate,2));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		}
		result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		if (result.getErrCode() < 0) {
			System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
		++seqNo;
		return result;
	}
	/**
	 * 
	* @Title: onSaveUnOrddParm
	* @Description: TODO(���ë@��IBS_ORDD���녢) �˷�
	* @author pangben
	* @param parm
	* @param parmIbsOrdm
	* @param parmIbsOrdd
	* @param caseNo
	* @param connection
	* @param flg
	* @return
	* @throws
	 */
	private TParm onSaveUnOrddParm(TParm parm,TParm parmIbsOrdd,
			String caseNo,TConnection connection,TParm numberParm,double diffQty,double dosageQty,String statCode){
		//�µ����
		String CTZ1 = parm.getValue("CTZ1_CODE");
		String CTZ2 = parm.getValue("CTZ2_CODE");
		String CTZ3 = parm.getValue("CTZ3_CODE");
		double lumpworkRate=parm.getDouble("LUMPWORK_RATE");//�ײ��ۿ�
		// �õ�ϵͳʱ��
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String orderNo=SystemTool.getInstance().getNo("ALL", "ODI", "ORDER_NO","ORDER_NO");
		//ѭ��������ϸ��
		int seqNo=numberParm.getInt("SEQ_NO");
		int maxCaseNoSeq=numberParm.getInt("CASE_NO_SEQ");
        BIL bil = new BIL();
        TParm result=new TParm();
        TParm insertIbsOrddNegativeParm = new TParm();	
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null 
				? new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));						
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty*-1);
		
		double  ownRate =0.00;//�����ڸ���סԺ�Ǽ����õ��ۿ۽��м���
		//ҩƷ��Ѫ�Ѹ������ͳ��
		if(null!=parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0)
				&&parmIbsOrdd.getValue("SYS_FEE_CAT1_TYPE",0).equals("PHA")||
				null!=parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0)&&parmIbsOrdd.getValue("CHARGE_HOSP_CODE",0).equals("RA")){
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
					parmIbsOrdd.getValue("ORDER_CODE", 0));
		}else{
			ownRate=lumpworkRate;//������ ����סԺ�Ǽ����õ��ۿ�ͳ��
		}
//		double ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE", 0) ,
//				parmIbsOrdd.getValue("ORDER_CODE", 0));//������ ��һ��ݺ͵ڶ����ͳ��
		insertIbsOrddNegativeParm.setData("OWN_PRICE",insertIbsOrddNegativeParm.getDouble("OWN_PRICE"));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",insertIbsOrddNegativeParm.getDouble("NHI_PRICE"));
		insertIbsOrddNegativeParm.setData("OWN_AMT",insertIbsOrddNegativeParm.getDouble("OWN_AMT"));
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","N");
		insertIbsOrddNegativeParm.setData("BILL_EXE_FLG","Y");
		result =IBSOrdmTool.getInstance().insertdataLumpworkDExe(insertIbsOrddNegativeParm,connection);
        if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
        }
        
        //ִ��״̬�޸�  BILL_EXE_FLG
		result = getExeBillExeFlg(caseNo,false, parmIbsOrdd.getValue("ORDER_CODE",0), connection);
		if (result.getErrCode() < 0) {
	        	System.out.println(result.getErrName() + " " + result.getErrText());
                return result;
	    }
        ++seqNo;
        //���»���ۿۺ�Ľ��
        insertIbsOrddNegativeParm = new TParm();		
		insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null ?
				new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));					
		insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);						
		insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
		//getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,diffQty*-1);
		insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
		insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
		insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
		ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
				parmIbsOrdd.getValue("ORDER_CODE",0));//�����������ݽ��м���
		insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
		insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
		insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
		result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
		if (result.getErrCode() < 0) {
        	System.out.println(result.getErrName() + " " + result.getErrText());
            return result;
		}
		//getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,dosageQty);
		if (diffQty+dosageQty!=0) {
			 ++seqNo;
	        //���»���ۿۺ�Ľ��
	        insertIbsOrddNegativeParm = new TParm();		
			insertIbsOrddNegativeParm.setData("CASE_NO",parmIbsOrdd.getData("CASE_NO", 0) == null ?
					new TNull(String.class) : parmIbsOrdd.getData("CASE_NO", 0));					
			insertIbsOrddNegativeParm.setData("CASE_NO_SEQ",maxCaseNoSeq);						
			insertIbsOrddNegativeParm.setData("SEQ_NO",seqNo);
			getIbsOrddParm(insertIbsOrddNegativeParm, parmIbsOrdd, 0, sysDate,1,diffQty+dosageQty);
			insertIbsOrddNegativeParm.setData("ORDER_NO",orderNo);
			insertIbsOrddNegativeParm.setData("ORDER_SEQ","1");
			insertIbsOrddNegativeParm.setData("ORDERSET_GROUP_NO",0);
			insertIbsOrddNegativeParm.setData("FREQ_CODE",statCode);
			ownRate = bil.getOwnRate(CTZ1,CTZ2,CTZ3,parmIbsOrdd.getValue("HEXP_CODE",0) ,
					parmIbsOrdd.getValue("ORDER_CODE",0));//�����������ݽ��м���
			insertIbsOrddNegativeParm.setData("OWN_RATE",ownRate);
			insertIbsOrddNegativeParm.setData("TOT_AMT",StringTool.round(insertIbsOrddNegativeParm.getDouble("OWN_AMT")*ownRate,2));
			insertIbsOrddNegativeParm.setData("INCLUDE_FLG","Y");
			result =IBSOrdmTool.getInstance().insertdataLumpworkD(insertIbsOrddNegativeParm,connection);
			if (result.getErrCode() < 0) {
				System.out.println(result.getErrName() + " " + result.getErrText());
	            return result;
	        }
		}
		++seqNo;
		return result;
	}
	/**
	 * ����ҽ��ʹ��
	 * @param insertIbsOrddNegativeParm
	 * @param parmIbsOrdd
	 * @param m
	 * @param sysDate
	 * @param mas
	 * @param dosageQty
	 */
	private void getIbsOrddParmOne(TParm insertIbsOrddNegativeParm,
			TParm parmIbsOrddNew,int m,Timestamp sysDate,int mas,double dosageQty,TParm parmIbsOrdd){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO",0));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", 0));
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrddNew.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDER_CODE", m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrddNew.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDER_CAT1_CODE", m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrddNew.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("CAT1_TYPE", m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrddNew.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDERSET_CODE", m));
		if (parmIbsOrddNew.getValue("ORDER_CODE",m).equals(parmIbsOrddNew.getValue("ORDERSET_CODE",m))) {
			insertIbsOrddNegativeParm.setData("INDV_FLG","N");
		}else{
			insertIbsOrddNegativeParm.setData("INDV_FLG","Y");
		}
		
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", 0));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", 0));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",dosageQty*mas);
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrddNew.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE","");
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", 0));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",dosageQty*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrddNew.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrddNew.getDouble("OWN_PRICE",m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrddNew.getDouble("NHI_PRICE",m));
		insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", 0));
		insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", 0));
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrddNew.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrddNew.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", 0));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", 0));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_PRICE",m)*dosageQty*mas);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", 0));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", 0));
		insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", 0));
		insertIbsOrddNegativeParm.setData("OPT_USER","PACK_BATCH");
		insertIbsOrddNegativeParm.setData("OPT_TERM","127.0.0.1");
		insertIbsOrddNegativeParm.setData("COST_AMT",dosageQty*mas>=0?Math.abs(parmIbsOrdd.getDouble("COST_AMT",0)):-Math.abs(parmIbsOrdd.getDouble("COST_AMT",0)));
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrddNew.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrddNew.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", 0));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", 0));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", 0));
		insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", 0));
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", 0) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", 0));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");
	}
	/**
	 * 
	* @Title: getIbsOrddParm
	* @Description: TODO(���IBS_ORDD������)
	* @author pangben
	* @param insertIbsOrddNegativeParm
	* @param parm
	* @param parmIbsOrdd
	* @param m
	* @param sysDate
	* @param mas
	* @throws
	 */
	private void getIbsOrddParm(TParm insertIbsOrddNegativeParm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,int mas,double dosageQty){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrdd.getData("ORDER_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_NO",m));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ",parmIbsOrdd.getData("ORDER_SEQ", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_SEQ", m));
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
		insertIbsOrddNegativeParm.setData("INDV_FLG",parmIbsOrdd.getData("INDV_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INDV_FLG", m));
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrdd.getData("DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DEPT_CODE", m));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrdd.getData("STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("STATION_CODE", m));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrdd.getData("DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DR_CODE", m));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrdd.getData("EXE_DEPT_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DEPT_CODE", m));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrdd.getData("EXE_STATION_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_STATION_CODE", m));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrdd.getData("EXE_DR_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("EXE_DR_CODE", m));
		if (parmIbsOrdd.getValue("CAT1_TYPE",m).equals("PHA")) {//ҩƷ��������
			String sql="SELECT MEDI_QTY FROM PHA_TRANSUNIT WHERE ORDER_CODE='"+parmIbsOrdd.getData("ORDER_CODE", m)+"'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			insertIbsOrddNegativeParm.setData("MEDI_QTY",result.getDouble("MEDI_QTY",0)*dosageQty*mas);
		}else{
			insertIbsOrddNegativeParm.setData("MEDI_QTY",dosageQty*mas);
		}
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrdd.getData("FREQ_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("FREQ_CODE", m));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS",1*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",dosageQty*mas);
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("OWN_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("NHI_PRICE",m));
		insertIbsOrddNegativeParm.setData("OWN_FLG",parmIbsOrdd.getData("OWN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("OWN_FLG", m));
		insertIbsOrddNegativeParm.setData("BILL_FLG",parmIbsOrdd.getData("BILL_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("BILL_FLG", m));
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrdd.getData("BEGIN_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("BEGIN_DATE", m));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrdd.getData("END_DATE", m) == null ? new TNull(Timestamp.class) : parmIbsOrdd.getData("END_DATE", m));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_PRICE",m)*dosageQty*mas);
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrdd.getData("REQUEST_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_FLG", m));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrdd.getData("REQUEST_NO", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REQUEST_NO", m));
		insertIbsOrddNegativeParm.setData("INV_CODE",parmIbsOrdd.getData("INV_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("INV_CODE", m));
		insertIbsOrddNegativeParm.setData("OPT_USER","PACK_BATCH");
		insertIbsOrddNegativeParm.setData("OPT_TERM","127.0.0.1");
		insertIbsOrddNegativeParm.setData("COST_AMT",dosageQty*mas>=0?Math.abs(parmIbsOrdd.getDouble("COST_AMT",m)):-Math.abs(parmIbsOrdd.getDouble("COST_AMT",m)));
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrdd.getData("COST_CENTER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("COST_CENTER_CODE", m));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrdd.getData("SCHD_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("SCHD_CODE", m));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrdd.getData("CLNCPATH_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CLNCPATH_CODE", m));
		insertIbsOrddNegativeParm.setData("DS_FLG",parmIbsOrdd.getData("DS_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DS_FLG", m));
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrdd.getData("KN_FLG", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("KN_FLG", m));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");
	}
	/**
	 * ��ѯ����������
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 * caowl
	 */
	public int selMaxOrderSetGroupNo(String caseNo) {
		String sql = " SELECT MAX(TO_NUMBER(ORDERSET_GROUP_NO)) AS ORDERSET_GROUP_NO FROM IBS_ORDD WHERE CASE_NO = '"
				+ caseNo + "' AND ORDERSET_CODE IS NOT NULL";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount()>0) {
			return result.getInt("ORDERSET_GROUP_NO",0);
		}
		return -1;
	}
	/**
	 * ����ҽ�������ײ�
	 * @param insertIbsOrddNegativeParm
	 * @param parm
	 * @param parmIbsOrdd
	 * @param m
	 * @param sysDate
	 * @param parmIbsOrddOld
	 */
	private void getIbsOrddParm(TParm insertIbsOrddNegativeParm,TParm parm,
			TParm parmIbsOrdd,int m,Timestamp sysDate,TParm parmIbsOrddOld){
		insertIbsOrddNegativeParm.setData("BILL_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("EXEC_DATE",sysDate);
		insertIbsOrddNegativeParm.setData("ORDER_NO",parmIbsOrddOld.getValue("ORDER_NO",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("ORDER_NO", 0));
		insertIbsOrddNegativeParm.setData("ORDER_SEQ","");
		insertIbsOrddNegativeParm.setData("ORDER_CODE",parmIbsOrdd.getData("ORDER_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CODE", m));
		insertIbsOrddNegativeParm.setData("ORDER_CAT1_CODE",parmIbsOrdd.getData("ORDER_CAT1_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CAT1_CODE", m));
		insertIbsOrddNegativeParm.setData("CAT1_TYPE",parmIbsOrdd.getData("CAT1_TYPE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("CAT1_TYPE", m));
		insertIbsOrddNegativeParm.setData("ORDERSET_CODE",parmIbsOrdd.getData("ORDERSET_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDERSET_CODE", m));
		if (parmIbsOrdd.getValue("ORDER_CODE",m).equals(parmIbsOrdd.getValue("ORDERSET_CODE",m))) {
			insertIbsOrddNegativeParm.setData("INDV_FLG","N");
		}else{
			insertIbsOrddNegativeParm.setData("INDV_FLG","Y");
		}
		insertIbsOrddNegativeParm.setData("DEPT_CODE",parmIbsOrddOld.getValue("DEPT_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("DEPT_CODE", 0));
		insertIbsOrddNegativeParm.setData("STATION_CODE",parmIbsOrddOld.getValue("STATION_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("STATION_CODE", 0));
		insertIbsOrddNegativeParm.setData("DR_CODE",parmIbsOrddOld.getValue("DR_CODE",0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("EXE_DEPT_CODE",parmIbsOrddOld.getData("EXE_DEPT_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_DEPT_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_STATION_CODE",parmIbsOrddOld.getData("EXE_STATION_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_STATION_CODE",0));
		insertIbsOrddNegativeParm.setData("EXE_DR_CODE",parmIbsOrddOld.getData("EXE_DR_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("EXE_DR_CODE", 0));
		insertIbsOrddNegativeParm.setData("MEDI_QTY",parmIbsOrdd.getDouble("MEDI_QTY",m)*parm.getDouble("DOSAGE_QTY"));
		insertIbsOrddNegativeParm.setData("MEDI_UNIT",parmIbsOrdd.getData("MEDI_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("MEDI_UNIT", m));
		insertIbsOrddNegativeParm.setData("DOSE_CODE",parmIbsOrdd.getData("DOSE_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSE_CODE", m));
		insertIbsOrddNegativeParm.setData("FREQ_CODE",parmIbsOrddOld.getData("FREQ_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("FREQ_CODE", 0));
		insertIbsOrddNegativeParm.setData("TAKE_DAYS","1");
		insertIbsOrddNegativeParm.setData("DOSAGE_QTY",parmIbsOrdd.getDouble("DOSAGE_QTY",m)*parm.getDouble("DOSAGE_QTY"));
		insertIbsOrddNegativeParm.setData("DOSAGE_UNIT",parmIbsOrdd.getData("DOSAGE_UNIT", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("DOSAGE_UNIT", m));
		insertIbsOrddNegativeParm.setData("NHI_PRICE",parmIbsOrdd.getDouble("OWN_PRICE",m));
		insertIbsOrddNegativeParm.setData("OWN_FLG","Y");
		insertIbsOrddNegativeParm.setData("BILL_FLG","Y");
		insertIbsOrddNegativeParm.setData("REXP_CODE",parmIbsOrdd.getData("REXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("REXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BILL_NO","");
		insertIbsOrddNegativeParm.setData("HEXP_CODE",parmIbsOrdd.getData("HEXP_CODE", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("HEXP_CODE", m));
		insertIbsOrddNegativeParm.setData("BEGIN_DATE",parmIbsOrddOld.getData("BEGIN_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrddOld.getData("BEGIN_DATE", 0));
		insertIbsOrddNegativeParm.setData("END_DATE",parmIbsOrddOld.getData("END_DATE", 0) == null ? new TNull(Timestamp.class) : parmIbsOrddOld.getData("END_DATE", 0));
		insertIbsOrddNegativeParm.setData("OWN_AMT",parmIbsOrdd.getDouble("OWN_AMT",m));
		insertIbsOrddNegativeParm.setData("REQUEST_FLG",parmIbsOrddOld.getData("REQUEST_FLG", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("REQUEST_FLG", 0));
		insertIbsOrddNegativeParm.setData("REQUEST_NO",parmIbsOrddOld.getData("REQUEST_NO", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("REQUEST_NO", 0));
		insertIbsOrddNegativeParm.setData("INV_CODE","");
		insertIbsOrddNegativeParm.setData("OPT_USER",parm.getValue("OPT_USER"));
		insertIbsOrddNegativeParm.setData("OPT_TERM",parm.getValue("OPT_TERM"));
		insertIbsOrddNegativeParm.setData("COST_AMT",0);
		insertIbsOrddNegativeParm.setData("ORDER_CHN_DESC",parmIbsOrdd.getData("ORDER_CHN_DESC", m) == null ? new TNull(String.class) : parmIbsOrdd.getData("ORDER_CHN_DESC", m));
		insertIbsOrddNegativeParm.setData("COST_CENTER_CODE",parmIbsOrddOld.getData("COST_CENTER_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("COST_CENTER_CODE", 0));
		insertIbsOrddNegativeParm.setData("SCHD_CODE",parmIbsOrddOld.getData("SCHD_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("SCHD_CODE", 0));
		insertIbsOrddNegativeParm.setData("CLNCPATH_CODE",parmIbsOrddOld.getData("CLNCPATH_CODE", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("CLNCPATH_CODE", 0));
		insertIbsOrddNegativeParm.setData("DS_FLG","N");
		insertIbsOrddNegativeParm.setData("KN_FLG",parmIbsOrddOld.getData("KN_FLG", 0) == null ? new TNull(String.class) : parmIbsOrddOld.getData("KN_FLG", 0));
		insertIbsOrddNegativeParm.setData("INCLUDE_EXEC_FLG","Y");

	}
}
